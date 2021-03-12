(ns scimmer.mapping.schema-card.styles
  (:require [scimmer.theme :refer [sizes]]))

(defn schema-card []
  {:grid-row       "1/3"
   :display        "flex"
   :flex-direction "column"
   :height         "100%"})

(defn body []
  {:flex 1})



