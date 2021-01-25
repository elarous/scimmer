(ns scimmer.app-db
  (:require [malli.util :as mu]
            [malli.core :as m]))

(def db
  {:mapping
             {:type     :map,
              :children [[:locale {:scimmer.services.schema/mapping :profile/locale} {:type string?}]
                         [:userName {:scimmer.services.schema/mapping :user/user_name} {:type string?}]
                         [:name
                          nil
                          {:type     :map,
                           :children [[:formatted {:scimmer.services.schema/mapping :profile/display_name} {:type string?}]
                                      [:familyName {:scimmer.services.schema/mapping :user/family_name} {:type string?}]
                                      [:givenName {:scimmer.services.schema/mapping :user/first_name} {:type string?}]]}]
                         [:emails
                          nil
                          {:type     :vector,
                           :children [{:type       :multi,
                                       :properties {:dispatch :type},
                                       :children   [[:work
                                                     nil
                                                     {:type     :map,
                                                      :children [[:type nil {:type :=, :children [:work]}]
                                                                 [:value {:scimmer.services.schema/mapping :user/email} {:type string?}]]}]
                                                    [:testEmail
                                                     nil
                                                     {:type     :map,
                                                      :children [[:type nil {:type :=, :children [:test]}]
                                                                 [:value {:scimmer.services.schema/mapping :profile/test_email} {:type string?}]]}]
                                                    [:personal
                                                     nil
                                                     {:type     :map,
                                                      :children [[:type nil {:type :=, :children [:personal]}]
                                                                 [:value
                                                                  {:scimmer.services.schema/mapping :profile/personal_email}
                                                                  {:type string?}]]}]]}]}]]}
   :another {:hi "there"}

   :resource {:id           "0ea136ad-061d-45f1-8d92-db5627a156f2"
              :externalId   "8a61fbf4-543c-4ed2-94a4-74838d6ba8ef"
              :userName     "karim"
              :locale       "en"
              :name         {:familyName "El Arbaoui"
                             :givenName  "Oussama"
                             :formatted  "Oussama-EL-Arbaoui"}
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
                             :contractStartDate "11-11-2011"}}})

(comment
  (def schema (:mapping db))
  (mu/to-map-syntax schema)

  (m/properties (schema)),)

