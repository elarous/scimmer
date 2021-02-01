(ns scimmer.mapping.schema-card.array-attrs-section.array-attr.views
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [herb.core :refer [<class join]]
            ["grommet" :refer [Anchor Grommet Button Heading Select Grid Box TextInput FormField]]
            ["grommet-icons" :refer [AddCircle Trash]]
            [scimmer.mapping.schema-card.input.views :refer [input]]
            [scimmer.mapping.schema-card.array-attrs-section.array-attr.styles :as stl]
            [scimmer.mapping.schema-card.array-attrs-section.array-attr.events]))

(defn array-attr-item [{:keys [group mapped-to type on-change on-remove]}]
  [:div {:class (<class stl/array-attr-inputs)}
   [input {:name        "group"
           :placeholder "Group"
           :value       group
           :on-change   (partial on-change :group)}]
   [input {:name        "type"
           :placeholder "Type"
           :value       (name type)
           :on-change   (partial on-change :type)}]
   [input {:name        "mappedTo"
           :placeholder "Mapped To"
           :value       mapped-to
           :on-change   (partial on-change :mapped-to)}]
   [:> Anchor {:icon     (r/create-element Trash #js {:className (<class stl/trash-icon)
                                                      :size      "xsmall"})
               :on-click on-remove}]])

(defn array-attr [id & body]
  [:div {:class (<class stl/array-attr)}
   body
   [:div {:class (<class stl/add-btn-container)}
    [:> Anchor {:on-click #(rf/dispatch [:mapping/>add-sub-item id])
                :icon     (r/create-element AddCircle #js {:className (<class stl/add-btn)})}]]])

