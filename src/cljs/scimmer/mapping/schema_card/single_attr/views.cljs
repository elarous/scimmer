(ns scimmer.mapping.schema-card.single-attr.views
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [herb.core :refer [<class join]]
            ["grommet" :refer [Anchor Grommet Button Heading Select Grid Box TextInput FormField]]
            [scimmer.mapping.schema-card.single-attr.events]
            [scimmer.mapping.schema-card.input.views :refer [input]]
            [scimmer.mapping.schema-card.single-attr.styles :as stl]))

(defn single-attr [{:keys [collection mapped-to on-collection-change on-mapped-to-change]}]
  [:div {:class (<class stl/single-attr)}
   [input {:name        "collection"
           :placeholder "Collection"
           :value       collection
           :on-change   #(on-collection-change (-> % .-target .-value))}]
   [input {:name        "mapped-to"
           :placeholder "Mapped To"
           :value       mapped-to
           :on-change   #(on-mapped-to-change (-> % .-target .-value))}]])

