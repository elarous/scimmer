(ns scimmer.app-db
  (:require [malli.util :as mu]
            [malli.core :as m]))

(def db
  {:mapping  {:type     :map,
              :children [[:id #:scimmer.services.schema{:mapping :user/uuid} {:type uuid?}]
                         [:externalId #:scimmer.services.schema{:mapping :user/source_id} {:type string?}]
                         [:userName #:scimmer.services.schema{:mapping :user/user_name} {:type string?}]
                         [:locale #:scimmer.services.schema{:mapping :custom_fields/locale} {:type string?}]
                         [:active #:scimmer.services.schema{:mapping :user/active} {:type boolean?}]
                         [:title #:scimmer.services.schema{:mapping :user/job} {:type string?}]
                         [:name
                          nil
                          {:type     :map,
                           :children [[:formatted #:scimmer.services.schema{:mapping :user/formatted_name} {:type string?}]
                                      [:familyName #:scimmer.services.schema{:mapping :user/last_name} {:type string?}]
                                      [:givenName #:scimmer.services.schema{:mapping :user/name} {:type string?}]]}]
                         [:displayName #:scimmer.services.schema{:mapping :user/full_name} {:type string?}]
                         [:emails
                          nil
                          {:type     :vector,
                           :children [{:type       :multi,
                                       :properties {:dispatch :type},
                                       :children   [[:work
                                                     nil
                                                     {:type     :map,
                                                      :children [[:type nil {:type :=, :children [:work]}]
                                                                 [:value
                                                                  #:scimmer.services.schema{:mapping :user/email}
                                                                  {:type string?}]]}]
                                                    [:personal
                                                     nil
                                                     {:type     :map,
                                                      :children [[:type nil {:type :=, :children [:personal]}]
                                                                 [:value
                                                                  #:scimmer.services.schema{:mapping :custom_fields/email_personal}
                                                                  {:type string?}]]}]]}]}]
                         [:phoneNumbers
                          nil
                          {:type     :vector,
                           :children [{:type       :multi,
                                       :properties {:dispatch :type},
                                       :children   [[:work
                                                     nil
                                                     {:type     :map,
                                                      :children [[:type nil {:type :=, :children [:work]}]
                                                                 [:value
                                                                  #:scimmer.services.schema{:mapping :custom_fields/phone_number}
                                                                  {:type string?}]]}]
                                                    [:mobile
                                                     nil
                                                     {:type     :map,
                                                      :children [[:type nil {:type :=, :children [:mobile]}]
                                                                 [:value
                                                                  #:scimmer.services.schema{:mapping :custom_fields/mobile_number}
                                                                  {:type string?}]]}]]}]}]]}

   :schema   {#uuid "0ea136ad-061d-45f1-8d92-db5627a156f2"
              {:type      :single
               :name      "id"
               :mapped-to "uuid"
               :group     "user"}
              #uuid "aaaaaaa-061d-45f1-8d92-db5627a156f2"
              {:type      :object
               :name      "name"
               :sub-attrs {#uuid "459e1f73-4356-4422-b60b-6f006908daa6"
                           {:name      "familyName"
                            :mapped-to "last_name"
                            :group     "user"}}}
              #uuid "bbbabbb-061d-45f1-8d92-db5627a156f2"
              {:type      :array
               :name      "phoneNumbers"
               :sub-items {#uuid "de72b3bd-4ce3-4ea0-b90b-337d4fbebc5f"
                           {:mapped-to "mobile_phone"
                            :type      "mobile"
                            :group     "user"}}}}


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

