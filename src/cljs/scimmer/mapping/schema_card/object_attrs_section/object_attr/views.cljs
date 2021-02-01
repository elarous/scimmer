(ns scimmer.mapping.schema-card.object-attrs-section.object-attr.views
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [herb.core :refer [<class join]]
            ["grommet" :refer [Anchor Grommet Button Heading Select Grid Box TextInput FormField]]
            ["grommet-icons" :refer [AddCircle Trash]]
            [scimmer.mapping.schema-card.object-attrs-section.object-attr.styles :as stl]
            [scimmer.mapping.schema-card.input.views :refer [input]]
            [scimmer.mapping.schema-card.object-attrs-section.object-attr.events]))


(defn object-inputs [{:keys [group mapped-to on-change]}]
  [:div {:class (<class stl/object-subattr-inputs)}
   [input {:name        "group"
           :placeholder "Group"
           :value       group
           :on-change   (partial on-change :group)}]
   [input {:name        "mapped to"
           :placeholder "Mapped To"
           :value       mapped-to
           :on-change   (partial on-change :mapped-to)}]])


(defn object-attr [id & body]
  [:div {:class (<class stl/object-attr)}
   body
   [:div {:class (<class stl/add-btn-container)}
    [:> Anchor {:on-click #(rf/dispatch [:mapping/>add-sub-attr id])
                :icon     (r/create-element AddCircle #js {:className (<class stl/add-btn)})}]]])

(defn sub-attr
  ([name body]
   (sub-attr name {} body))
  ([name {:keys [on-change on-remove]} body]
   [:div {:class (<class stl/object-subattr)}
    [:div {:class (<class stl/head-container)}
     [:input {:class         (join (<class stl/object-attr-title)
                                   (<class stl/head-input))
              :type          "text"
              :on-change     on-change
              :default-value name}]
     [:> Anchor {:icon     (r/create-element Trash #js {:className (<class stl/trash-icon)
                                                        :size      "xsmall"})
                 :on-click on-remove}]]
    body]))
