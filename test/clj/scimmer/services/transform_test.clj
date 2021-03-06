(ns scimmer.transform-test
  (:require
   [clojure.test :refer [use-fixtures deftest testing is]]
   [ring.mock.request :as req]
   [scimmer.config :refer [env]]
   [scimmer.db.core :refer [*db*]]
   [scimmer.handler :refer [app app-routes]]
   [scimmer.services.schemas :as schs]
   [scimmer.services.mapping :as mapping]
   [luminus-migrations.core :as migrations]
   [next.jdbc :as jdbc]
   [muuntaja.core :as m]
   [mount.core :as mount]))

(use-fixtures
  :once
  (fn [f]
    (mount/start #'env #'*db* #'app-routes)
    (migrations/migrate ["migrate"] (select-keys env [:database-url]))
    (f)
    (mount/stop #'env #'*db* #'app-routes)))

(def schema
  {:id         #uuid "0ea136ad-061d-45f1-8d92-0056272256f9"
   :resource   "user"
   :name       "first_schema"
   :is-default false
   :attrs      [{:id           #uuid "0ea136ad-061d-45f1-8d92-db5627a156f2"
                 :name         "id"
                 :mapped-to    "uuid"
                 :collection   "user"
                 :schema-id    #uuid "0ea136ad-061d-45f1-8d92-005627a156f9"
                 :extension-id nil
                 :type         :single}
                {:id           #uuid "11122333-061d-45f1-8d92-db5627a156f2"
                 :name         "userName"
                 :mapped-to    "user_name"
                 :collection   "user"
                 :schema-id    #uuid "0ea136ad-061d-45f1-8d92-005627a156f9"
                 :extension-id nil
                 :type         :single}
                {:id           #uuid "0aaaaaaa-061d-45f1-8d92-db5627a156f2"
                 :name         "name"
                 :schema-id    #uuid "0ea136ad-061d-45f1-8d92-005627a156f9"
                 :extension-id nil
                 :sub-attrs    [{:id             #uuid "0aaaaaaa-061d-45f1-8d92-db5627a156f2"
                                 :name           "familyName"
                                 :mapped-to      "last_name"
                                 :collection     "profile"
                                 :object-attr-id #uuid "0aaaaaaa-061d-45f1-8d92-db5627a156f2"}
                                {:id             #uuid "b69a9c73-b305-4285-8292-d07a66f2f4e6"
                                 :name           "givenName"
                                 :mapped-to      "first_name"
                                 :collection     "profile"
                                 :object-attr-id #uuid "0aaaaaaa-061d-45f1-8d92-db5627a156f2"}]
                 :type         :object}
                {:id           #uuid "0bbbabbb-061d-45f1-8d92-db5627a156f2"
                 :name         "phoneNumbers"
                 :schema-id    #uuid "0ea136ad-061d-45f1-8d92-005627a156f9"
                 :extension-id nil
                 :sub-items    [{:id            #uuid "de72b3bd-4ce3-4ea0-b90b-337d4fbebc5f"
                                 :type          "mobile"
                                 :mapped-to     "mobile_phone"
                                 :collection    "user"
                                 :array-attr-id #uuid "0bbbabbb-061d-45f1-8d92-db5627a156f2"}]
                 :type         :array}]
   :extensions [{:id        #uuid "42fb5049-d2fa-4579-ad44-6a479a281d04"
                 :name      "enterprise"
                 :schema-id #uuid "0ea136ad-061d-45f1-8d92-005627a156f9"
                 :attrs     [{:id           #uuid "7bca8ecb-b999-4d52-84f7-59132dda6f5f"
                              :name         "organization"
                              :mapped-to    "organization"
                              :collection   "profile"
                              :schema-id    nil
                              :extension-id #uuid "42fb5049-d2fa-4579-ad44-6a479a281d04"
                              :type         :single}
                             {:id           #uuid "7d8f6aca-10d1-4d1c-a574-a969e059254d"
                              :name         "manager"
                              :schema-id    nil
                              :extension-id #uuid "42fb5049-d2fa-4579-ad44-6a479a281d04"
                              :sub-attrs    [{:id             #uuid "5106ebd8-3b1a-4c88-9f8a-30baec5506dc"
                                              :name           "value"
                                              :mapped-to      "manager_id"
                                              :collection     "user"
                                              :object-attr-id #uuid "7d8f6aca-10d1-4d1c-a574-a969e059254d"}]
                              :type         :object}]}]})

(def resource
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
   :phoneNumbers [{:value "000000" :type "mobile"}
                  {:value "12341234" :type "work"}]
   :urn:ietf:params:scim:schemas:extension:enterprise:2.0:User
   {:department     "Tech"
    :organization   "Google. Inc"
    :employeeNumber "003FTDH2"
    :costCenter     nil
    :division       nil
    :manager        {:value       "0F2342SLDJF23"
                     :displayName "Jamil the boss"}}})

(deftest resource-to-entities
  (jdbc/with-transaction [t-conn *db* {:rollback-only true}]
    (schs/upsert-schema! schema)
    (testing "Resource to entities for SCIM PUT"
      (let [req-body     {:schema-id (:id schema)
                          :resource  resource}
            response     ((app) (-> (req/request :post "/api/resource_to_entities")
                                (req/json-body req-body)))
            decoded-resp (m/decode-response-body response)]
        (testing "Response shape"
          (is (= 200 (:status response)))
          (is (= #{:meta :entities} (set (keys decoded-resp))))
          (is (= #{:user :profile} (-> decoded-resp :entities keys set))))
        (testing "Meta data"
          (is (:id schema) (get-in decoded-resp [:meta :schema-id])))
        (testing "Single attributes"
          (is (:id resource) (get-in decoded-resp [:entities :user :uuid])))
        (testing "Object attributes"
          (is (and (= (get-in resource [:name :familyName])
                      (get-in decoded-resp [:entities :profile :last_name]))
                   (= (get-in resource [:name :givenName])
                      (get-in decoded-resp [:entities :profile :first_name])))))
        (testing "Array attributes"
          (is (-> resource :phoneNumbers first) (get-in decoded-resp [:entities :user :mobile_phone])))
        (testing "Extension attributes"
          (is (get-in resource [:urn:ietf:params:scim:schemas:extension:enterprise:2.0:User :organization])
              (get-in decoded-resp [:entities :profile :organization])))))))

