(ns scimmer.mapping.entities-card.styles
  (:require [scimmer.theme :refer [sizes fonts colors]]))

(defn entities []
  {:height      "100%"
   :background (:grey-1 colors)
   :font-size   (sizes 2)
   :font-family (:code fonts)
   :overflow    "auto"})
