(ns scimmer.mapping.schema-card.styles
  (:require [scimmer.theme :refer [sizes colors]]))

(defn schema-card []
  {:grid-row       "1/3"
   :display        "flex"
   :flex-direction "column"
   :height         "100%"})

(defn body []
  {:flex 1})

(defn schema-id []
  {:margin         (sizes 2)
   :padding        (sizes 1)
   :border         (str "1px solid " (:grey-2 colors))
   :border-radius  (sizes 1)
   :display        "flex"
   :flex-direction "column"
   :align-items    "center"})

(defn schema-id-lbl []
  {:color  (:tertiary colors)
   :margin (sizes 1)})

(defn schema-id-val []
  {:color (:primary colors)})

