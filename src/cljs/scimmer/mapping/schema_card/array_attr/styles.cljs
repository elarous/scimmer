(ns scimmer.mapping.schema-card.array-attr.styles
  (:require [scimmer.theme :refer [colors fonts sizes shadows]]))

(defn array-attr []
  {:display        "flex"
   :flex-direction "column"})

(defn array-attr-inputs []
  {:display         "flex"
   :justify-content "space-between"
   :align-items     "center"})

(defn add-btn-container  []
  {:display "flex"
   :justify-content "center"})

(defn add-btn []
  ^{:pseudo {:hover {:stroke (:secondary colors)}}}
  {:stroke (:grey-2 colors)
   :transition "0.3s stroke"})

(defn trash-icon []
  ^{:pseudo {:hover {:stroke (:secondary colors)}}}
  {:stroke (str (:grey-3 colors))
   :transition "0.3s stroke"
   :height (sizes 2)
   :width (sizes 2)})