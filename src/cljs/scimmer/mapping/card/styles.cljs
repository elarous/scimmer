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
  {:background      (:grey-1 colors)
   :display         "flex"
   :justify-content "center"
   :align-items     "center"
   :padding         (sizes 2)})

(defn card []
  {:border-radius (sizes 1)
   :box-shadow    (:medium shadows)
   :background    "#fff"})


