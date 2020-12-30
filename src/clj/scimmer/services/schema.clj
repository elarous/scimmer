(ns scimmer.services.schema
  (:require [malli.core :as m]
            [malli.util :as mu]))

;; schemas
(def full-user
  [:map
   [:id {::mapping :user/uuid} uuid?]
   [:externalId {::mapping :user/source_id} string?]
   [:userName {::mapping :user/email} string?]
   [:locale {::mapping :custom_fields/locale} string?]
   [:active {::mapping :user/active} boolean?]
   [:title {::mapping :user/job} string?]
   [:name
    [:map
     [:formatted {::mapping :user/formatted_name} string?]
     [:familyName {::mapping :user/last_name} string?]
     [:givenName {::mapping :user/name} string?]]]
   [:displayName {::mapping :user/full_name} string?]
   [:emails
    [:vector
     [:multi {:dispatch :type}
      [:work [:map
              [:type [:= :work]]
              [:value {::mapping :user/email}  string?]]]
      #_[:mobile [:map
                  [:type [:= :mobile]]
                  [:value {::mapping :custom_fields/email_personal} string?]]]]]]
   [:phoneNumbers
    [:vector
     [:multi {:dispatch :type}
      [:work [:map
              [:type [:= :work]]
              [:value {::mapping :custom_fields/phone_number}  string?]]]
      #_[:mobile [:map
                  [:type [:= :mobile]]
                  [:value {::mapping :profile/mobile_number} string?]]]]]]])

(def enterprise-ext
  [:map {:extension :urn:ietf:params:scim:schemas:extension:enterprise:2.0:User}
   [:employeeNumber {::mapping :custom_fields/employee_number} string?]
   [:costCenter string?]
   [:organization {::mapping :custom_fields/organization} string?]
   [:division string?]
   [:department {::mapping :custom_fields/department} string?]
   [:manager
    [:map
     [:value {::mapping :user/manager_uuid} string?]
     [:$ref string?]
     [:displayName string?]]]])

(def javelo-ext
  [:map {:extension :urn:ietf:params:scim:schemas:extension:javelo:2.0:User}
   [:managerUserName {::mapping :user/manager_username} string?]
   [:status {::mapping :custom_fields/status} string?]
   [:grade {::mapping :custom_fields/grade} string?]
   [:seniorityDate {::mapping :custom_fields/first_hired_on} string?]
   [:contractStartDate {::mapping :custom_fields/current_hired_on} string?]])



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
(def user-schema (-> full-user
                     (include-extension enterprise-ext)
                     (include-extension javelo-ext)))

(def user-schema-map (mu/to-map-syntax user-schema))
