(ns scimmer.mapping.schema-card.single-attrs-section.single-attr.styles
  (:require [scimmer.theme :refer [sizes]]))

(defn single-attr []
  {:display         "flex"
   :justify-content "space-between"
   :align-items     "center"
   :border-radius   (sizes 1)})
