(ns scimmer.services.schema
  (:require [malli.core :as m]))

;; schemas
(def full-user
  [:map
   [:id {::mapping :user/uuid} uuid?]
   [:externalId {::mapping :user/source_id} string?]
   [:userName {::mapping :user/email} string?]
   [:locale {::mapping :custom_fields/locale} string?]
   [:name
    [:map
     [:formatted {::mapping :user/formatted_name} string?]
     [:familyName {::mapping :user/name} string?]
     [:givenName {::mapping :user/full_name} string?]]]
   [:displayName {::mapping :user/full_name} string?]
   [:emails
    [:vector
     [:multi {:dispatch :type}
      [:work [:map
              [:type [:= :work]]
              [:value {::mapping :custom_fields/email}  string?]]]
      [:mobile [:map
                [:type [:= :mobile]]
                [:value {::mapping :custom_fields/email_personal} string?]]]]]]
   [:phoneNumbers
    [:vector
     [:multi {:dispatch :type}
      [:work [:map
              [:type [:= :work]]
              [:value {::mapping :custom_fields/work_number}  string?]]]
      [:mobile [:map
                [:type [:= :mobile]]
                [:value {::mapping :profile/mobile_number} string?]]]]]]])

(def enterprise-ext
  [:map {:extension "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User"}
   [:employeeNumber string?]
   [:costCenter string?]
   [:organization string?]
   [:division string?]
   [:department string?]
   [:manager
    [:map
     [:value string?]
     [:$ref string?]
     [:displayName string?]]]])

;; Operations schemas
(def update-schema
  [:map
   [:Operations
    [:vector
     [:map
      [:op [:enum "replace" "add" "delete"]]
      [:path string?]
      [:value [:or string? number? boolean?]]]]]])

;; helper functions
(defn include-extension [user-schema ext-schema]
  (conj user-schema [(get (m/properties ext-schema) :extension) ext-schema]))

;; combined schemas
(def user-schema (include-extension full-user enterprise-ext))
;; transformations
(defn- symbol->keyword [schema]
  (let [fn-str (name (m/-form schema))]
    (keyword (subs fn-str 0 (-> fn-str count dec)))))

(defn malli->scim-patch [schema]
  (let [result (atom {})]
    (m/walk schema
            (fn [schema path _children _b]
              (let [cleaned-path (vec (remove #{:malli.core/in} path))
                    full-path (if (> (count cleaned-path) 1)
                                (-> (interpose [:type :attributes] cleaned-path) flatten vec)
                                cleaned-path)
                    multi-valued-path (when (some #{:malli.core/in} path)
                                        (->> path (take-while #(not (#{:malli.core/in} %))) vec))
                    r (cond-> @result
                              (nil? (get-in @result full-path)) (assoc-in full-path {:type schema})
                              (symbol? (m/-form schema)) (assoc-in full-path (symbol->keyword schema))
                              multi-valued-path (assoc-in (conj multi-valued-path :multi-valued) true))]
                (reset! result r))))
    {:attributes @result}))

(comment)
