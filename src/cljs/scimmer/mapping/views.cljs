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
   [attribute [single-attr]]
   [attribute [object-attr]]
   [attribute [array-attr]]])

(defn mapping-page []
  [:div {:class (<class stl/container)}
   [:> Grommet {:theme (clj->js stl/grommet-theme)
                :class (<class stl/grommet)}
    [:div {:class (<class stl/grid)}
     [schema-card]
     [card {} [header "Resource" "User"]]
     [card {} [header "Entities" "User"]]]]])

