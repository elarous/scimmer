(ns scimmer.mapping.schema-card.extensions.extension.styles
  (:require [scimmer.theme :refer [colors fonts sizes]]))

(defn label []
  {:text-align  "center"
   :font-family (:headers fonts)
   :color       (:secondary colors)})