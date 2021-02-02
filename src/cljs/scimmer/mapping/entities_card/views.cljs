(ns scimmer.mapping.entities-card.views
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
            [scimmer.mapping.entities-card.styles :as stl]
            [scimmer.mapping.card.views :refer [card header]]
            [scimmer.mapping.schema-card.views :refer [schema-card]]
            [scimmer.mapping.resource-card.views :refer [resource-card]]))

(defn entities-card []
  [card {}
   [header "Entities" "User"]
   [:pre {:class (<class stl/entities)}
    (.stringify js/JSON (clj->js @(rf/subscribe [:mapping/entities])) nil 2)]])
