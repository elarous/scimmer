(ns scimmer.mapping.schema-card.header.styles
  (:require [scimmer.theme :refer [sizes]]))

(defn search-input []
  {:padding (sizes 1)
   :width   "100% !important"})

(defn search-item []
  {:padding (sizes 2)})

