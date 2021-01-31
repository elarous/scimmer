(ns scimmer.mapping.schema-card.single-attrs-section.single-attr.views
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [herb.core :refer [<class join]]
            ["grommet" :refer [Anchor Grommet Button Heading Select Grid Box TextInput FormField]]
            [scimmer.mapping.schema-card.input.views :refer [input]]
            [scimmer.mapping.schema-card.single-attrs-section.single-attr.styles :as stl]))

(defn single-attr [{:keys [value on-change]}]
  [:div {:class (<class stl/single-attr)}
   [input {:name        "entity"
           :placeholder "Entity"
           :value       (:entity value)
           :on-change   (partial on-change :entity)}]
   [input {:name        "mapping"
           :placeholder "Mapping"
           :value       (:mapping value)
           :on-change   (partial on-change :mapping)}]])
