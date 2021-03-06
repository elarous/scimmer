(ns scimmer.mapping.schema-card.attribute.styles
  (:require [scimmer.theme :refer [colors fonts sizes shadows]]))

(defn container []
  {:display        "flex"
   :flex-direction "column"
   :margin         (sizes 4)})

(defn head-container []
  {:display "flex"
   :justify-content "space-between"})

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
             :focus {:border     (str "1px solid " (:grey-3 colors))
                     :background (:grey-1 colors)}}}
  {:border        "1px solid transparent"
   :border-radius (sizes 0.5)
   :transition    "0.3s border, 0.3s background"
   :padding       (sizes 0.5)
   :max-width     (sizes 20)
   :outline       "none"})

(defn collapse-icon [collapsed?]
  {:cursor    "pointer"
   :transform (if collapsed? "rotate(-90deg)" "none")})

(defn body []
  {:padding      (sizes 1)
   :margin-left  (sizes 2)
   :margin-right (sizes 2)})

(defn trash-icon []
  ^{:pseudo {:hover {:stroke (:secondary colors)}}}
  {:stroke (str (:grey-3 colors))
   :transition "0.3s stroke"
   :height (sizes 2)
   :width (sizes 2)})
