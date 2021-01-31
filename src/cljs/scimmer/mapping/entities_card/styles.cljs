(ns scimmer.mapping.entities-card.styles
  (:require [scimmer.theme :refer [sizes fonts]]))

(defn entities []
  {:height      "100%"
   :font-size   (sizes 2)
   :font-family (:code fonts)
   :overflow    "auto"})
