(ns scimmer.mapping.schema-card.top-actions.styles
  (:require [scimmer.theme :refer [sizes]]))

(defn top-actions []
  {:display         "flex"
   :justify-content "flex-end"
   :align-items     "center"
   :margin          (sizes 1)})
