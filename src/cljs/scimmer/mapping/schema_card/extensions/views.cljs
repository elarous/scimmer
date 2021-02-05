(ns scimmer.mapping.schema-card.extensions.views
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [herb.core :refer [<class]]
            ["grommet" :refer [Anchor]]
            ["grommet-icons" :refer [Add]]
            [scimmer.theme :refer [colors]]
            [scimmer.mapping.schema-card.extensions.subs]
            [scimmer.mapping.schema-card.extensions.events]
            [scimmer.mapping.schema-card.extensions.styles :as stl]
            [scimmer.mapping.schema-card.extensions.extension.views :refer [extension]]))

(defn extensions []
  [:div {:class (<class stl/extensions)}
   [:div {:class (<class stl/header)} "Extensions"]
   (for [ext @(rf/subscribe [:mapping/extensions])]
     ^{:key (:id ext)}
     [extension ext])
   [:div {:class (<class stl/footer)}
    [:> Anchor {:on-click #(rf/dispatch [:mapping/>add-ext])
                :margin   "medium"
                :size     "medium"
                :color    (:secondary colors)
                :label    "Add Extension"
                :icon     (r/create-element Add #js {:size "medium"})}]]])

