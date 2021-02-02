(ns scimmer.mapping.resource-card.styles
  (:require [scimmer.theme :refer [colors sizes fonts]]))

(defn resource-card-body []
  {:overflow "hidden"})

(defn resource-textarea []
  {:width                      "100%"
   :height                     "100%"
   :margin                     0
   :padding                    (sizes 1)
   :box-sizing                 "border-box"
   :color                      (:text-secondary colors)
   :font-size                  (sizes 2)
   :font-family                (:code fonts)
   :outline                    "none"
   :resize                     "none"
   :border-bottom-right-radius (sizes 1)
   :border-bottom-left-radius  (sizes 1)
   :border                     "none"})
