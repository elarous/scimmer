(ns scimmer.services.mapping
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]
            [clojure.walk :as walk]
            [malli.generator :as mg]
            [scimmer.services.schema :as sch]
            [malli.core :as m]
            [malli.util :as mu]
            [malli.transform :as mt]
            [malli.transform :as mt]))

(def mapping (-> (io/resource "mapping.edn")
                 slurp
                 (edn/read-string)))

(defn- vec->map [v]
  (->> (map-indexed #(vector %1 %2) v)
       (reduce conj {})))

(defn keys-in
  "taken from based on example from: https://dnaeon.github.io/clojure-map-ks-paths/ with minor modifications"
  [m]
  (letfn [(children [node]
            (let [v (get-in m node)]
              (cond
                (map? v)
                (map (fn [x] (conj node x)) (keys v))
                (vector? v)
                (map (fn [x] (conj node x)) (keys (vec->map v)))
                :else [])))
          (branch? [node] (-> (children node) seq boolean))]
    (->> (keys m)
         (map vector)
         (mapcat #(tree-seq branch? children %)))))

(defn lookup-map [mapping]
  (->> (map (fn [v] [v (get-in mapping v)]) (keys-in mapping))
       (remove (fn [[k v]] (or (boolean? v) (string? v) (map? v) (vector? v))))
       (map (fn [[k v]] [v k]))
       (reduce conj {})))

(defn lookup-map-2 [mapping]
  (->> (map (fn [v] [v (get-in mapping v)]) (keys-in mapping))
       (remove (fn [[k v]] (or (boolean? v) (string? v) (map? v) (vector? v))))
       (into {})))

(defn extra-values [object lookup]
  (->> (map (fn [[k path]] [k (get-in object path)]) lookup)
       (reduce conj {})))

(defn object+mapping->entity [object mapping]
  (extra-values object (lookup-map mapping)))

;; public api
(defn map-resource->entities [resource mappings]
  (->> mappings
       (map (fn [[entity mapping]] (hash-map entity (object+mapping->entity resource mapping))))
       (reduce conj {})))

;; experimental
(defn- get-vals
  "Get the values of maps recursively"
  [m]
  (cond
    (map? m) (map (fn [[_ v]] (get-vals v)) m)
    (sequential? m) (map get-vals m)
    :else m))

(defn mappings->columns [mappings]
  (->>
    (map (fn [[entity ms]]
           [entity (->> (get-vals ms)
                        (flatten)
                        (filter keyword?)
                        (vec))]) mappings)
    (into {})))

;;

(def entities
  {:user        {:email                  "oussama+manager@javelo.io",
                 :email_personal         "mypersonal-email@google.com"
                 :deleted_at             nil,
                 :name                   "Oussama+Manager",
                 :invitation_accepted_at "2019-11-21T14:48:12.526+01:00",
                 :avatar_urls            {},
                 :never_signed_in        false,
                 :manager_id             1668,
                 :okr_master             false,
                 :id                     1659,
                 :last_name              "el arbaoui",
                 :current_hired_on       nil,
                 :company_manager        true,
                 :full_name              "Oussama El ARbaoui"
                 :locale                 "en"
                 :manager_username       "karimos@karimos.com"
                 :job_description        ""},
   :text_fields {:organization "cccccccccccc", :employee_number "bbbbbb", :job "aaaaa"}})


(defn fill-assoc-in [m path val]
  (loop [m m
         p path
         depth 1]
    (if (empty? p)
      (if (empty? (get-in m path))
        (assoc-in m path val)
        m)
      (recur (if (get-in m (vec (take depth path)))
               m
               (assoc-in m (vec (take depth path)) {}))
             (rest p)
             (inc depth)))))

(defn get-paths [schema]
  (let [paths (atom [])]
    (m/walk schema
            (fn [_schema path _walked _options]
              (swap! paths conj path)))
    @paths))



(def user-entity (:user entities))

(defn path-in-mapping [mapping attr]
  (some #(and (some #{attr} %) %) (keys-in mapping)))

(defn mapping-value [mapping attr]
  (let [p (path-in-mapping mapping attr)]
    (get-in mapping p)))

(defn attr-val [entity-k resource mappings attr & {:keys [many? type]}]
  (let [attr-in-mapping (mapping-value mappings attr)]
    (prn attr-in-mapping)
    (if many?
      (get resource
           (-> (some #(and (= type (:type %)) %) attr-in-mapping)
               (get :value)))
      (get resource attr-in-mapping))))

(some #{:userName} [:use :userName])
(lookup-map-2 mapping)

(attr-val :user (:user entities) mapping :emails :many? true :type "personal")
(attr-val :user (:user entities) mapping :id)


;;
(defn extra-key-from-path [paths]
  (prn paths)
  (map last paths))
;;


;; TODO: Get all attrs names (ex: displayName...) from the paths
;; then assoc to the write place
;; i need to handle the special case of vectors
(comment
  (reduce (fn [acc v]
            (prn "v->" v)
            (fill-assoc-in acc v
                           (attr-val :user (:user entities) mapping
                                     (extra-key-from-path v))))
          {} (get-paths sch/user-schema))
  ;;


  (mu/get sch/user-schema :displayName)
  (mu/find-first sch/user-schema (fn [schema path _]
                                   (when (vector? schema)
                                     (= (-> schema first) :emails))))

  (mu/get-in (m/schema sch/user-schema) [:emails :malli.core/in :work])

  (mu/in->paths (m/schema sch/user-schema) [:emails :value])
  (mu/path->in (m/schema sch/user-schema) [:emails])


  (m/walk
    sch/user-schema
    (m/schema-walker
      (fn [schema]
        (prn (-> schema m/properties)))))

  (get-vals [{:primary true, :type "dance", :value :email} {:type "+1234", :value :email_mobile}])
  (mappings->columns mapping)
  (sequential? '())
  (flatten (get-vals (vals mapping)))

  (walk/postwalk-demo mapping)
  (lookup-map mapping)
  (def user
    {:id           #uuid "0ea136ad-061d-45f1-8d92-db5627a156f2"
     :externalId   "8a61fbf4-543c-4ed2-94a4-74838d6ba8ef"
     :userName     "karim"
     :locale       "en"
     :name         {:familyName "El Arbaoui"
                    :givenName  "Oussama"}
     :displayName  "Kamaro"
     :emails       [{:value "ok@ok.com" :type "work" :primary true}
                    {:value "mobile@mobile.com" :type "mobile"}]
     :active       true
     :title        "Engineer"
     :phoneNumbers [{:value "12341234" :type "work"}
                    {:value "000000" :type "mobile"}]
     :urn:ietf:params:scim:schemas:extension:enterprise:2.0:User
                   {:department     "Tech"
                    :organization   "Google. Inc"
                    :employeeNumber "003FTDH2"
                    :manager        {:value "0F2342SLDJF23"}}
     :urn:ietf:params:scim:schemas:extension:javelo:2.0:User
                   {:managerUserName   "manager@manager.com"
                    :status            "on going"
                    :grade             "leader"
                    :seniorityDate     "12-12-2000"
                    :contractStartDate "11-11-2011"}})

  (def mapping
    {:id          :uuid
     :externalId  :source_id
     :userName    :email
     :name        {:givenName :name :familyName :last_name}
     :displayName :full_name
     :emails      [{:primary true :type "work" :value :email}
                   {:type "personal" :value :email_personal}]
     :active      :active
     :locale      :locale})




  (map-resource->entities user mapping)
  (mappings->columns mapping)



  (mg/generate [:map
                [:id [:enum 12]]
                [:name [:map
                        [:formatted [:enum "karim"]]
                        [:familyName [:enum "samir"]]]]])

  (mg/generate [:map
                [:value [:enum "ok@ok.com" "hi@hi.com"]]
                [:type [:enum "work" "mobile"]]
                [:primary {:optional true} boolean?]])

  (mg/generate
    [:vector {:min 2 :max 2}
     [:map [:type [:enum :work :personal]] [:value string?]]]
    {:size 10})

  (mg/generate
    [:vector
     [:map [:type :work] [:value string?]]
     [:map [:type :personal] [:value string?]]])

  (mg/generate
    [:vector {:min 2, :max 2}
     [:multi {:dispatch :type}
      [:work [:map [:type [:= :work]] [:value string?]]]
      [:personal [:map [:type [:= :personal]] [:value string?]]]]])

  (mg/generate sch/full-user)


  (m/walk sch/user-schema
          (m/schema-walker
            (fn [schema]
              (prn schema)
              [:enum "ok"])))


  (mu/get-in sch/user-schema [:name :formatted])

  (mu/in->paths (m/schema sch/user-schema) [:emails :value])
  (mu/get-in (m/schema sch/user-schema) [:emails])

  (mu/to-map-syntax sch/user-schema))

