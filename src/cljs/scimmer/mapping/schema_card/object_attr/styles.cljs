(ns scimmer.mapping.schema-card.object-attr.styles
  (:require [scimmer.theme :refer [sizes colors fonts]]))

(defn object-attr []
  {:display        "flex"
   :flex-direction "column"})

(defn object-subattr-inputs []
  {:display         "flex"
   :justify-content "space-between"
   :align-items     "center"})

(defn add-btn-container []
  {:display         "flex"
   :justify-content "center"})

(defn add-btn []
  ^{:pseudo {:hover {:stroke (:secondary colors)}}}
  {:stroke     (:grey-2 colors)
   :transition "0.3s stroke"})

(defn object-attr-title []
  {:color       (str (:tertiary colors) " !important")
   :font-weight "bold"})

(defn object-subattr []
  {:display        "flex"
   :flex-direction "column"
   :margin-top     (sizes 1)
   :margin-bottom  (sizes 1)})

(defn head-container []
  {:display         "flex"
   :justify-content "space-between"})

(defn trash-icon []
  ^{:pseudo {:hover {:stroke (:secondary colors)}}}
  {:stroke     (str (:grey-3 colors))
   :transition "0.3s stroke"
   :height     (sizes 2)
   :width      (sizes 2)})

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
   :max-height    (sizes 4)
   :border-radius (sizes 0.5)
   :transition    "0.3s border, 0.3s background"
   :padding       (sizes 0.5)
   :max-width     (sizes 20)
   :outline       "none"})

