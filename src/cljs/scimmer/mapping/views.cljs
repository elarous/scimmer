(ns scimmer.mapping.views
  (:require ["react" :as re]
            [reagent.core :as reagent]
            [herb.core :refer [<class]]
            ["grommet" :refer [Grommet Button Heading Select Grid Box TextInput FormField]]
            [scimmer.mapping.styles :as stl]
            [scimmer.mapping.card.views :refer [card header]]
            [scimmer.mapping.attribute.views :refer [attribute single-attr object-attr array-attr]]))

(defn schema-card []
  [card {:class (<class stl/schema-card)}
   [header "Schema" "User"]
   [attribute "locale" [single-attr {:value     {:entity "my_locale" :mapping "profile"}
                                     :on-change #(js/console.log (-> % .-target .-value))}]]
   [attribute "userName" [object-attr {:firstName {:value     {:entity "first_name" :mapping "profile"}
                                                   :on-change #(js/console.log (-> % .-target .-value))}
                                       :lastName  {:value     {:entity "last_name" :mapping "profile"}
                                                   :on-change #(js/console.log (-> % .-target .-value))}}]]
   [attribute "email" [array-attr {:value [{:entity "primary_email" :type "work" :mapping "profile"}
                                           {:entity "secondary_email" :type "mobile" :mapping "profile"}]
                                   :on-change #(js/console.log (-> % .-target .-value))}]]])

(defn mapping-page []
  [:div {:class (<class stl/container)}
   [:> Grommet {:theme (clj->js stl/grommet-theme)
                :class (<class stl/grommet)}
    [:div {:class (<class stl/grid)}
     [schema-card]
     [card {} [header "Resource" "User"]]
     [card {} [header "Entities" "User"]]]]])

