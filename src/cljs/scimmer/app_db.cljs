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
                                                        {:type string?}]]}]]}]}]]}})

(comment
  (def schema (:mapping db))
  (mu/to-map-syntax schema)

  (m/properties (schema)),)

