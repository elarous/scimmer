(ns scimmer.mapping.views
  (:require ["react" :as re]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [herb.core :refer [<class]]
            ["grommet" :refer [Anchor Grommet Button Heading Select Grid Box TextInput FormField Layer]]
            ["grommet-icons" :refer [Add]]
            [scimmer.utils :refer [debounce]]
            [scimmer.theme :refer [colors]]
            [scimmer.mapping.subs]
            [scimmer.mapping.events]
            [scimmer.mapping.styles :as stl]
            [scimmer.mapping.card.views :refer [card header]]
            [scimmer.mapping.schema-card.views :refer [schema-card]]
            [scimmer.mapping.resource-card.views :refer [resource-card]]
            [scimmer.mapping.entities-card.views :refer [entities-card]]))

(defn mapping-page []
  [:div {:class (<class stl/container)}
   [:> Grommet {:theme (clj->js stl/grommet-theme)
                :class (<class stl/grommet)}
    [:div {:class (<class stl/grid)}
     ^{:key "schema-card"}
     [schema-card]
     ^{:key "resource-card"}
     [resource-card]
     ^{:key "entities-card"}
     [entities-card]]]])

