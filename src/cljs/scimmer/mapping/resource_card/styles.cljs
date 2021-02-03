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

(defn resource-container []
  {:height      "100%"
   :width       "100%"
   :overflow    "auto"
   :padding     (sizes 2)
   :font-size   (sizes 2)
   :border-bottom-right-radius (sizes 1)
   :border-bottom-left-radius  (sizes 1)
   :font-family (str (:code fonts) " !important")})

(defn resource-editor []
  {:font-family (:code fonts)
   :font-size   (sizes 4)})

