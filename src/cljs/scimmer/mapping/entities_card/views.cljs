(ns scimmer.mapping.entities-card.views
  (:require ["react" :as re]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [herb.core :refer [<class]]
            ["grommet" :refer [Anchor Grommet Button Heading Select Grid Box TextInput FormField Layer]]
            ["grommet-icons" :refer [Add]]
            ["react-json-pretty" :as json-pretty]
            [goog.string :as gstring]
            [goog.string.format]
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
    [:> json-pretty {:id    "json-pretty"
                     :data  @(rf/subscribe [:mapping/entities-json])
                     :theme #js {:main    (gstring/format "background: %s; color: %s;"
                                                          (:grey-1 colors)
                                                          (:text-secondary colors))
                                 :error   "line-height:1.3;color:#66d9ef;background:#272822;overflow:auto;"
                                 :key     (gstring/format "color: %s;" (:secondary colors))
                                 :string  (gstring/format "color: %s;" (:primary colors))
                                 :value   (gstring/format "color: %s;" (:text-secondary colors))
                                 :boolean "color:#ac81fe"}}]]])



