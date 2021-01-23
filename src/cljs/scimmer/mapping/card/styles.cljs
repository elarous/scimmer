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

(defn attr-header []
  {:margin           (sizes 1)
   :padding          (sizes 1)
   :background-color (:grey-2 colors)
   :color            (:primary colors)
   :border-radius    (sizes 1)
   :text-align       "center"
   :font-weight      "600"})

(defn attr-inputs-container []
  {:display          "flex"
   :background-color (:grey-1 colors)
   :border-radius    (sizes 1)
   :padding          (sizes 1)
   :margin-left      (sizes 2)
   :margin-right     (sizes 2)})

(defn attr-form-field []
  {:border  "none !important"
   :outline "none"
   :margin  (sizes 1)})

(defn attr-input []
  {:border  (str "1px solid " (:grey-3 colors))
   :padding (sizes 1)})

