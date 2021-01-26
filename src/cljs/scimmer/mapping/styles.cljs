(ns scimmer.mapping.styles
  (:require [scimmer.theme :refer [colors fonts sizes shadows]]))

;; Grommet theme
(def grommet-theme
  {:global    {:font {:family (:normal fonts)
                      :size   (sizes 2)
                      :height (sizes 2)}}
   :formField {:border {:position "none"}}})

(defn container []
  {:padding  (sizes 3)
   :overflow "hidden"})

(defn grommet []
  {:height "100%"})

(defn grid []
  {:display               "grid"
   :height                "100%"
   :grid-gap              (sizes 2)
   :grid-template-columns "50% 50%"
   :grid-template-rows    "50% 50%"})

(defn schema-card []
  {:grid-row "1/3"})

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

(defn entities []
  {:height      "100%"
   :font-size   (sizes 2)
   :font-family (:code fonts)
   :overflow    "auto"})

