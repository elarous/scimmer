(ns scimmer.mapping.attribute.styles
  (:require [scimmer.theme :refer [colors fonts sizes shadows]]))

(defn container []
  {:display        "flex"
   :flex-direction "column"
   :margin         (sizes 4)})

(defn head []
  {:display     "flex"
   :align-items "center"
   :font-family (:normal fonts)
   :font-size   (sizes 2)
   :color       (:primary colors)
   :font-weight "bold"})

(defn head-input []
  ^{:extend [head]
    :pseudo {:hover {:border (str "1px solid " (:grey-3 colors))}
             :focus {:border    (str "1px solid " (:grey-3 colors))
                     :background (:grey-1 colors)}}}
  {:border  "1px solid transparent"
   :border-radius (sizes 0.5)
   :transition "0.3s border, 0.3s background"
   :padding (sizes 0.5)
   :max-width (sizes 20)
   :outline "none"})

(defn collapse-icon [collapsed?]
  {:cursor    "pointer"
   :transform (if collapsed? "rotate(-90deg)" "none")})

(defn body []
  {:padding      (sizes 1)
   :margin-left  (sizes 2)
   :margin-right (sizes 2)})

(defn single-attr []
  {:display         "flex"
   :justify-content "space-between"
   :align-items     "center"
   :border-radius   (sizes 1)})

(defn object-attr []
  {:display        "flex"
   :flex-direction "column"})

(defn object-attr-title []
  {:color       (:tertiary colors)
   :font-weight "bold"})

(defn object-subattr []
  {:display        "flex"
   :flex-direction "column"
   :margin-top     (sizes 1)
   :margin-bottom  (sizes 1)})

(defn object-subattr-inputs []
  {:display         "flex"
   :justify-content "space-between"
   :align-items     "center"})

(defn array-attr []
  {:display        "flex"
   :flex-direction "column"})

(defn array-attr-inputs []
  {:display         "flex"
   :justify-content "space-between"
   :align-items     "center"})

(defn attr-form-field []
  {:border    "none !important"
   :flex      1
   :outline   "none"
   :font-size (sizes 0.2)
   :margin    (sizes 1)})

(defn attr-input []
  {:border  (str "1px solid " (:grey-3 colors))
   :padding (sizes 1)})
