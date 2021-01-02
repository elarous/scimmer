(ns scimmer.services.mapping-test
  (:require [clojure.test :refer :all]
            [malli.util :as mu]
            [scimmer.services.schema :as sch]
            [scimmer.services.mapping :as m]))


(deftest build-resource
  (testing "extracting a top level attribute"
    (let [user-name "Nadiro"
          resource {:userName user-name}
          entities (m/build-resource
                     (mu/to-map-syntax [:map [:userName {::sch/mapping :user/user-name} string?]]) resource [] {} {})]
      (is (contains? entities :user))
      (is (= (get-in entities [:user :user-name]) user-name))))

  (testing "extracting an attribute with a map as value"
    (let [vls {:formatted "Nabi KEITA"
               :family-name "keita"
               :given-name "nabi"}
          resource {:name {:formatted (:formatted vls)
                           :familyName (:family-name vls)
                           :givenName (:given-name vls)}}
          entities (m/build-resource
                     (mu/to-map-syntax
                       [:map
                        [:name
                         [:map
                          [:formatted {::sch/mapping :user/formatted_name} string?]
                          [:familyName {::sch/mapping :user/last_name} string?]
                          [:givenName {::sch/mapping :user/name} string?]]]])
                     resource [] {} {})]
      (is (contains? entities :user))
      (is (= (get-in entities [:user :formatted_name]) (:formatted vls)))
      (is (= (get-in entities [:user :last_name]) (:family-name vls)))
      (is (= (get-in entities [:user :name]) (:given-name vls))))))














