(ns scimmer.mapping.resource-card.views
  (:require ["react" :as re]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [herb.core :refer [<class]]
            ["grommet" :refer [Anchor Grommet Button Heading Select Grid Box TextInput FormField Layer]]
            ["grommet-icons" :refer [Add]]
            [scimmer.utils :refer [debounce]]
            [scimmer.theme :refer [colors]]
            [scimmer.mapping.resource-card.events]
            [scimmer.mapping.resource-card.styles :as stl]
            [scimmer.mapping.card.views :refer [card header]]
            [scimmer.mapping.schema-card.single-attrs-section.views :refer [singles-section]]
            [scimmer.mapping.schema-card.object-attrs-section.views :refer [objects-section]]
            [scimmer.mapping.schema-card.array-attrs-section.views :refer [arrays-section]]
            [scimmer.mapping.schema-card.top-actions.views :refer [top-actions]]
            [scimmer.mapping.schema-card.utils :refer [get-entity-mapping get-mapping-attr-item]]))

(defn resource-card []
  (r/with-let [contents (r/atom "")
               on-change (debounce
                           #(reset! contents (-> % .-target .-value))
                           #(rf/dispatch [:mapping/>set-resource @contents])
                           500)]
    [card {:body-class (<class stl/resource-card-body)}
     [header "Resource" "User"]
     [:textarea {:class     (<class stl/resource-textarea)
                 :default-value @(rf/subscribe [:mapping/resource-json])
                 :on-change on-change}]]))