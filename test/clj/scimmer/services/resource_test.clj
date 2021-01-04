(ns scimmer.services.resource-test
  (:require [scimmer.services.resource :as sut]
            [scimmer.services.schema :as sch]
            [clojure.test :refer :all]
            [malli.util :as mu]))

(deftest build-resource
  (testing "extracting a top level attribute"
    (let [locale "EN"
          entity {:user {:locale locale}}
          schema [:map [:locale {::sch/mapping :user/locale} string?]]
          resource (sut/build-resource (mu/to-map-syntax schema) entity)]
      (def r resource)
      (is (contains? resource :locale))
      (is (= (:locale resource) locale))))

  (testing "extracting a map value attribute"
    (let [vls {:formatted "John SmitH"
               :family-name "John"
               :given-name "Smith"}
          entity {:user {:formatted_name (:formatted vls)
                         :last_name (:family-name vls)
                         :name (:given-name vls)}}
          schema [:map
                  [:name
                   [:map
                    [:formatted {::sch/mapping :user/formatted_name} string?]
                    [:familyName {::sch/mapping :user/last_name} string?]
                    [:givenName {::sch/mapping :user/name} string?]]]]
          resource (sut/build-resource (mu/to-map-syntax schema) entity)]
      (def r resource)
      (is (= (get-in resource [:name :formatted]) (:formatted vls)))
      (is (= (get-in resource [:name :familyName]) (:family-name vls)))
      (is (= (get-in resource [:name :givenName]) (:given-name vls)))))

  (testing "extracting a vector value attribute"
    (let [vls {:work-number "+12121212"
               :mobile-number "+34343434"}
          entity {:user {:work_number (:work-number vls)
                         :mobile_number (:mobile-number vls)}}
          schema [:map
                  [:phoneNumbers
                   [:vector
                    [:multi {:dispatch :type}
                     [:work [:map
                             [:type [:= :work]]
                             [:value {::sch/mapping :user/work_number}  string?]]]
                     [:mobile [:map
                               [:type [:= :mobile]]
                               [:value {::sch/mapping :user/mobile_number} string?]]]]]]]
          resource (sut/build-resource (mu/to-map-syntax schema) entity)]
      (is (contains? resource :phoneNumbers))
      (is 2 (count (:phoneNumbers resource)))
      (is (= (:value (some #(and (= (:type %) "work") %) (:phoneNumbers resource)))
             (:work-number vls)))
      (is (= (:value (some #(and (= (:type %) "mobile") %) (:phoneNumbers resource)))
             (:mobile-number vls))))))

