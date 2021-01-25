(ns scimmer.services.resource
  "Create the resource based on the given schema and entities filled with values"
  (:require [scimmer.services.schema :as sch]
            [malli.provider :as mp]
            [malli.util :as mu]))

(defn value-by-key [entities ns-k]
  (let [ns (keyword (namespace ns-k))
        k (keyword (name ns-k))
        path [ns k]]
    (get-in entities path)))

(defn build-dispatch [element entities]
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

(defmethod build-resource :attr [[name props contents] entities]
  (if (:children contents)
    (map #(build-resource % entities) (:children contents))
    (let [k (::sch/mapping props)]
      [name (and k (value-by-key entities k))])))

(defmethod build-resource :map-no-name [contents entities]
  (let [children-result (map #(build-resource % entities) (:children contents))]
    (into {} children-result)))

(defmethod build-resource :map [[name props contents] entities]
  (let [children-result (map #(build-resource % entities) (:children contents))]
    [name (into {} children-result)]))

(defmethod build-resource :vector [[name props contents] entities]
  (let [children-result (build-resource (-> contents :children first) entities)]
    [name children-result]))

(defmethod build-resource :multi-no-name [contents entities]
  (let [children-result (map #(build-resource % entities) (:children contents))]
    (mapv second children-result)))

(defmethod build-resource := [[tag-name props contents] entities]
  [tag-name (-> contents :children first name)])

;;
(comment
  (build-resource (mu/to-map-syntax sch/full-user) entities)


  (def entities
    {:user
     {:email                  "oussama+manager@javelo.Io",
      :email_personal         "mypersonal-email@google.com"
      :deleted_at             nil,
      :name                   "Oussama+Manager",
      :formatted_name         "OussAMA El arBaOui"
      :invitation_accepted_at "2019-11-21T14:48:12.526+01:00",
      :avatar_urls            {},
      :never_signed_in        false,
      :manager_id             1668,
      :okr_master             false,
      :id                     1659,
      :uuid                   "1234-1234-1234"
      :source_id              "EBD23423423"
      :last_name              "el arbaoui",
      :current_hired_on       nil,
      :company_manager        true,
      :full_name              "Oussama El ARbaoui"
      :locale                 "en"
      :manager_username       "karimos@karimos.com"
      :job_description        ""}
     :custom_fields
     {:phone_number "+111111111"
      :mobile_number "+222222222"}}))


