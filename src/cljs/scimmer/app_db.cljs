(ns scimmer.app-db
  (:require [malli.util :as mu]
            [malli.core :as m]))

(def db
  {:mapping
   {:type     :map,
    :children [[:locale {:scimmer.services.schema/mapping :profile/locale} {:type string?}]
               [:name
                nil
                {:type     :map,
                 :children [[:formatted {} {:type string?}]
                            [:familyName {} {:type string?}]
                            [:givenName {} {:type string?}]]}]
               [:emails
                nil
                {:type     :vector,
                 :children [{:type       :multi,
                             :properties {:dispatch :type},
                             :children   [[:work
                                           nil
                                           {:type     :map,
                                            :children [[:type nil {:type :=, :children [:work]}]
                                                       [:value {} {:type string?}]]}]
                                          [:personal
                                           nil
                                           {:type     :map,
                                            :children [[:type nil {:type :=, :children [:personal]}]
                                                       [:value
                                                        {}
                                                        {:type string?}]]}]]}]}]]}})

(comment
  (def schema (:mapping db))
  (mu/to-map-syntax schema)

  (m/properties (schema)),)

