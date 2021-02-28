(ns scimmer.mapping.schema-card.object-attr.views
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [herb.core :refer [<class join]]
            ["grommet" :refer [Anchor Grommet Button Heading Select Grid Box TextInput FormField]]
            ["grommet-icons" :refer [AddCircle Trash]]
            [scimmer.mapping.schema-card.object-attr.styles :as stl]
            [scimmer.mapping.schema-card.input.views :refer [input]]
            [scimmer.mapping.schema-card.object-attr.events]))


(defn object-inputs [{:keys [collection mapped-to on-collection-change on-mapped-to-change]}]
  [:div {:class (<class stl/object-subattr-inputs)}
   [input {:name        "collection"
           :placeholder "Collection"
           :value       collection
           :on-change   on-collection-change}]
   [input {:name        "mapped to"
           :placeholder "Mapped To"
           :value       mapped-to
           :on-change   on-mapped-to-change}]])


(defn object-attr [{:keys [on-add-sub-attr]} & body]
  [:div {:class (<class stl/object-attr)}
   body
   [:div {:class (<class stl/add-btn-container)}
    [:> Anchor {:on-click on-add-sub-attr
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
