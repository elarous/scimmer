(ns scimmer.mapping.card.styles
  (:require [scimmer.theme :refer [colors fonts sizes shadows]]))

;; card components

(defn header-part []
  {:margin      (sizes 0.2)
   :font-weight "bold"})

(defn header-name []
  ^{:extend [header-part]}
  {:color (:secondary colors)})

(defn header-of []
  ^{:extend [header-part]}
  {:color (:text-secondary colors)})

(defn header-object []
  ^{:extend [header-part]}
  {:color (:highlight colors)})

(defn header []
  {:background              (:grey-1 colors)
   :display                 "flex"
   :justify-content         "center"
   :align-items             "center"
   :border-top-left-radius  (sizes 1)
   :border-top-right-radius (sizes 1)
   :padding                 (sizes 2)})

(defn body []
  {:flex           1
   :display        "flex"
   :flex-direction "column"
   :overflow       "auto"})

(defn card []
  {:border-radius  (sizes 1)
   :height         "100%"
   :box-shadow     (:medium shadows)
   :display        "flex"
   :flex-direction "column"
   :background     "#fff"})


