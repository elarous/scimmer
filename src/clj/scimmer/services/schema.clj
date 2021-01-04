(ns scimmer.services.schema
  (:require [malli.core :as m]
            [malli.util :as mu]))

;; schemas
(def full-user
  [:map
   [:id {::mapping :user/uuid} uuid?]
   [:externalId {::mapping :user/source_id} string?]
   [:userName {::mapping :user/user_name} string?]
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
      [:personal [:map
                  [:type [:= :personal]]
                  [:value {::mapping :custom_fields/email_personal} string?]]]]]]
   [:phoneNumbers
    [:vector
     [:multi {:dispatch :type}
      [:work [:map
              [:type [:= :work]]
              [:value {::mapping :custom_fields/phone_number}  string?]]]
      [:mobile [:map
                [:type [:= :mobile]]
                [:value {::mapping :custom_fields/mobile_number} string?]]]]]]])

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
(def user-schema (-> full-user (include-extension enterprise-ext)))


(def user-schema-map (mu/to-map-syntax user-schema))

