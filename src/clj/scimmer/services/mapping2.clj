(ns scimmer.services.mapping2
  (:require [scimmer.services.schema :as sch]
            [malli.provider :as mp]
            [malli.util :as mu]
            [scimmer.services.resource :as res]))

(defn keyword->name [ns-k]
  (some-> ns-k name keyword))

(defn keyword->ns [ns-k]
  (some-> ns-k namespace keyword))
;;

(defn build-dispatch [element resource lookup-path result opts]
  (cond
    (and (vector? element) (first element))
    (case (:type (-> element next next first))
      :map :map
      :vector :vector
      := :=
      :attr)
    (map? element)
    (case (:type element)
      :map :map-no-name
      :multi :multi-no-name
      :attr-no-name)))

(defmulti build-resource #'build-dispatch)

(defmethod build-resource :attr [[name props contents] resource lookup-path result {:keys [in-vec?]}]
  (if (:children contents)
    (map #(build-resource % resource) (:children contents))
    (let [key (get props ::sch/mapping)
          lookup-path (if in-vec? lookup-path (conj lookup-path name))
          full-path [(keyword->ns key) (keyword->name key)]]
      ;; Only assign attributes with a mapping namespaced keyword
      (if key
        (assoc-in result full-path (get-in resource lookup-path))
        result))))

(defmethod build-resource :map-no-name [contents resource lookup-path result {:keys [in-vec?] :as opts}]
  (loop [children (:children contents)
         result result]
    (if (empty? children)
      result
      (recur (rest children)
             (merge-with into result
                         (build-resource (first children) resource lookup-path result opts))))))

(defmethod build-resource :map [[name props contents] resource lookup-path result {:keys [in-vec?] :as opts}]
  (let [path (conj lookup-path (if in-vec? :value name))]
    (loop [children (:children contents)
           result result]
      (if (empty? children)
        result
        (recur (rest children)
               (merge-with into result
                           (build-resource (first children) resource path result opts)))))))

(defmethod build-resource :vector [[name props contents] resource lookup-path result opts]
  (let [children-result
        (build-resource (-> contents :children first)
                        resource
                        (conj lookup-path name)
                        result
                        (assoc opts :in-vec? true))]
    (merge-with into result children-result)))

(defmethod build-resource :multi-no-name [contents resource lookup-path result opts]
  (let [children-result (map-indexed #(build-resource %2
                                                      resource
                                                      (conj lookup-path %1)
                                                      result
                                                      opts)
                                     (:children contents))]
    (merge-with into result
                (apply (partial merge-with into) children-result))))

(defmethod build-resource := [[name props contents] resource lookup-path result opts]
  nil)

;;
(comment
  (build-resource (mu/to-map-syntax sch/full-user) user [] {:user {:age 22}} {})
  (build-resource (mu/to-map-syntax sch/user-schema) user [] {:user {:age 22}} {})

  (def user
    {:id           "0ea136ad-061d-45f1-8d92-db5627a156f2"
     :externalId   "8a61fbf4-543c-4ed2-94a4-74838d6ba8ef"
     :userName     "karim"
     :locale       "en"
     :name         {:familyName "El Arbaoui"
                    :givenName  "Oussama"
                    :formatted "Oussama-EL-Arbaoui"}
     :displayName  "Kamaro"
     :emails       [{:value "work@work.com" :type "work" :primary true}
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
                    :contractStartDate "11-11-2011"}}))


