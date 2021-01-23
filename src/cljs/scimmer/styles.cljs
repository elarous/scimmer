(ns scimmer.styles
  (:require [scimmer.theme :refer [colors sizes fonts shadows]]))

(defn page-style []
  {:display               "grid"
   :height                "100%"
   :position              "absolute"
   :top                   "0"
   :left                  "0"
   :grid-template-columns (str (sizes 30) " auto")})
