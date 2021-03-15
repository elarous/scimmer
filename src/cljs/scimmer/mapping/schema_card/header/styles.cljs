(ns scimmer.mapping.schema-card.header.styles
  (:require [scimmer.theme :refer [sizes]]))

(defn header-container []
  {:display         "flex"
   :align-items     "center"
   :justify-content "space-between"})

(defn search-input []
  {:padding (sizes 1)
   :width   "100% !important"})

(defn search-item []
  {:padding (sizes 2)})



