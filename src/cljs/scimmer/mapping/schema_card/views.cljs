(ns scimmer.mapping.schema-card.views
  (:require ["react" :as re]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [herb.core :refer [<class]]
            [scimmer.mapping.subs]
            [scimmer.mapping.events]
            [scimmer.mapping.schema-card.styles :as stl]
            [scimmer.mapping.card.views :refer [card header]]
            [scimmer.mapping.schema-card.single-attrs-section.views :refer [singles-section]]
            [scimmer.mapping.schema-card.object-attrs-section.views :refer [objects-section]]
            [scimmer.mapping.schema-card.array-attrs-section.views :refer [arrays-section]]
            [scimmer.mapping.schema-card.top-actions.views :refer [top-actions]]
            [scimmer.mapping.schema-card.footer.views :refer [footer]]
            [scimmer.mapping.schema-card.extensions.views :refer [extensions]]
            [scimmer.mapping.schema-card.events]))

(defn schema-card []
  (let [schema @(rf/subscribe [:mapping/schema])
        set-attr #(rf/dispatch [:mapping/>set-attr %1 %2])
        remove-attr #(rf/dispatch [:mapping/>remove-attr %1])]
    [card {:class (<class stl/schema-card)}
     [header (:name schema) (:resource schema)]
     [top-actions]
     [:div {:class (<class stl/body)}
      [singles-section {:set-attr set-attr :remove-attr remove-attr}]
      [objects-section {:set-attr set-attr :remove-attr remove-attr}]
      [arrays-section {:set-attr set-attr :remove-attr remove-attr}]
      [extensions]]
     [footer {:on-save #(rf/dispatch [:mapping/>save])}]]))

