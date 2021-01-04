(ns scimmer.services.scim-patch-test
  (:require [scimmer.services.scim-patch :as sut]
            [scimmer.services.schema :as sch]
            [clojure.test :refer :all]
            [malli.util :as mu]))

(deftest schema->scim-patch-schema
  (testing "transform Malli schema to scim-patch (library) schema"
    (let [schema [:map
                  [:id {::sch/mapping :user/uuid} uuid?]
                  [:name
                   [:map
                    [:familyName {::sch/mapping :user/last_name} string?]
                    [:givenName {::sch/mapping :user/name} string?]]]
                  [:emails
                   [:vector
                    [:multi {:dispatch :type}
                     [:work [:map
                             [:type [:= :work]]
                             [:value {::sch/mapping :user/email}  string?]]]
                     [:personal [:map
                                 [:type [:= :personal]]
                                 [:value {::sch/mapping :user/email_personal} string?]]]]]]]

          patch-schema (sut/schema->scim-patch-schema (mu/to-map-syntax schema))]
      (def p patch-schema)
      (is (= {:attributes
              {:id {:type :string},
               :name
               {:type
                {:attributes
                 {:familyName {:type :string}, :givenName {:type :string}}}},
               :emails
               {:type {:attributes {:type {:type :string}, :value {:type :string}}},
                :multi-valued true}}}
             patch-schema)))))

