(ns scimmer.services.schema-round-trip-test
  (:require [scimmer.services.schema :as sut]
            [scimmer.services.mapping :as mapping]
            [scimmer.services.resource :as resource]
            [malli.util :as mu]
            [clojure.test :refer :all]
            [scimmer.services.schema :as sch]))

(def schema
  (mu/to-map-syntax
   [:map
    [:id {::sch/mapping :user/uuid} uuid?]
    [:externalId {::sch/mapping :user/source_id} string?]
    [:userName {::sch/mapping :user/user_name} string?]
    [:locale {::sch/mapping :user/locale} string?]
    [:active {::sch/mapping :user/active} boolean?]
    [:title {::sch/mapping :user/job} string?]
    [:name
     [:map
      [:formatted {::sch/mapping :user/formatted_name} string?]
      [:familyName {::sch/mapping :user/last_name} string?]
      [:givenName {::sch/mapping :user/name} string?]]]
    [:displayName {::sch/mapping :user/full_name} string?]
    [:emails
     [:vector
      [:multi {:dispatch :type}
       [:work [:map
               [:type [:= :work]]
               [:value {::sch/mapping :user/email}  string?]]]
       [:personal [:map
                   [:type [:= :personal]]
                   [:value {::sch/mapping :profile/email_personal} string?]]]]]]
    [:phoneNumbers
     [:vector
      [:multi {:dispatch :type}
       [:work [:map
               [:type [:= :work]]
               [:value {::sch/mapping :profile/phone_number}  string?]]]
       [:mobile [:map
                 [:type [:= :mobile]]
                 [:value {::sch/mapping :profile/mobile_number} string?]]]]]]
    [:urn:ietf:params:scim:schemas:extension:enterprise:2.0:User
     [:map
      [:employeeNumber {::sch/mapping :user/employee_number} string?]
      [:costCenter {::sch/mapping :user/cost_center}  string?]
      [:organization {::sch/mapping :profile/organization} string?]
      [:division  {::sch/mapping :profile/division} string?]
      [:department {::sch/mapping :profile/department} string?]
      [:manager
       [:map
        [:value {::sch/mapping :user/manager_uuid} string?]
        [:displayName {::sch/mapping :user/manager_display_name} string?]]]]]]))

(deftest resource-entities
  (testing "Resource -> Entities -> Resource"
    (let [original-resource
          {:id           "0ea136ad-061d-45f1-8d92-db5627a156f2"
           :externalId   "8a61fbf4-543c-4ed2-94a4-7838d6ba8ef"
           :userName     "karim"
           :locale       "en"
           :name         {:familyName "El Arbaoui"
                          :givenName  "Oussama"
                          :formatted  "Oussama-EL-Arbaoui"}
           :displayName  "Kamaro"
           :emails       [{:value "work@work.com" :type "work"}
                          {:value "personal@personal.com" :type "personal"}]
           :active       true
           :title        "Engineer"
           :phoneNumbers [{:value "12341234" :type "work"}
                          {:value "000000" :type "mobile"}]
           :urn:ietf:params:scim:schemas:extension:enterprise:2.0:User
           {:department     "Tech"
            :organization   "Google. Inc"
            :employeeNumber "003FTDH2"
            :costCenter     nil
            :division       nil
            :manager        {:value       "0F2342SLDJF23"
                             :displayName "Jamil the boss"}}}
          entities     (mapping/build-resource schema original-resource [] {} {})
          new-resource (resource/build-resource schema entities)]
      (is (= original-resource new-resource))))

  (testing "Entities -> Resource -> Entities"
    (let [original-entities
          {:user
           {:email                "oussama+manager@company.Io",
            :active               true
            :name                 "Oussama+Manager",
            :formatted_name       "OussAMA El arBaOui"
            :uuid                 "1234-1234-1234"
            :source_id            "EBD23423423"
            :last_name            "el arbaoui",
            :full_name            "Oussama El ARbaoui"
            :locale               "en"
            :user_name            "oussos"
            :manager_display_name "karimos"
            :manager_uuid         "1234-1234-1234-1234"
            :job                  "Engineer"
            :employee_number      "0007"
            :cost_center          "cost_center"}
           :profile
           {:phone_number   "+111111111"
            :mobile_number  "+222222222"
            :email_personal "mypersonal-email@google.com"
            :department     "Engineering"
            :division       "div2"
            :organization   "BR .Inc"}}
          user-resource (resource/build-resource schema original-entities)
          new-entities  (mapping/build-resource schema user-resource [] {} {})]
      (is (= original-entities new-entities)))))

