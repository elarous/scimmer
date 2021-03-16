(ns scimmer.mapping.schema-card.views
  (:require [re-frame.core :as rf]
            [herb.core :refer [<class]]
            [scimmer.mapping.subs]
            [scimmer.mapping.events]
            [scimmer.mapping.schema-card.styles :as stl]
            [scimmer.mapping.card.views :refer [card]]
            [scimmer.mapping.schema-card.single-attrs-section.views :refer [singles-section]]
            [scimmer.mapping.schema-card.object-attrs-section.views :refer [objects-section]]
            [scimmer.mapping.schema-card.array-attrs-section.views :refer [arrays-section]]
            [scimmer.mapping.schema-card.top-actions.views :refer [top-actions]]
            [scimmer.mapping.schema-card.footer.views :refer [footer]]
            [scimmer.mapping.schema-card.extensions.views :refer [extensions]]
            [scimmer.mapping.schema-card.header.views :refer [header]]
            [scimmer.mapping.schema-card.events]
            [scimmer.mapping.schema-card.subs]))

(defn schema-id []
  (let [schema @(rf/subscribe [:mapping/schema])]
    [:div {:class (<class stl/schema-id)}
     [:div {:class (<class stl/schema-id-lbl)} "ID"]
     [:div {:class (<class stl/schema-id-val)} (when (:id schema) (.toString (:id schema)))]]))

(defn schema-card []
  (let [set-attr    #(rf/dispatch [:mapping/>set-attr %1 %2])
        remove-attr #(rf/dispatch [:mapping/>remove-attr %1])]
    [card {:class (<class stl/schema-card)}
     [header]
     [schema-id]
     [top-actions]
     [:div {:class (<class stl/body)}
      [singles-section {:set-attr set-attr :remove-attr remove-attr}]
      [objects-section {:set-attr set-attr :remove-attr remove-attr}]
      [arrays-section {:set-attr set-attr :remove-attr remove-attr}]
      [extensions]]
     [footer {:on-save #(rf/dispatch [:mapping/>save])}]]))

