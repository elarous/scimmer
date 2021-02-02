(ns scimmer.mapping.schema-card.add-attr.styles
  (:require [scimmer.theme :refer [sizes colors fonts shadows]]))

(defn add-modal []
  {:padding (sizes 2)})

(defn add-modal-title []
  {:font-size   (sizes 2)
   :color       (:primary colors)
   :font-weight "bold"})

(defn buttons []
  {:display    "flex"
   :margin-top (sizes 4)})

(defn button []
  ^{:pseudo {:hover {:border     (str "1px solid " (:secondary colors))
                     :box-shadow (:medium shadows)}}}
  {:padding       (sizes 2)
   :height        (sizes 15)
   :width         (sizes 15)
   :margin        (sizes 1)
   :color         (:text-secondary colors)
   :font-weight   "bold"
   :font-family   (:main fonts)
   :background    (:grey-2 colors)
   :border        (str "1px solid " (:grey-3 colors))
   :outline       "none"
   :transition    "0.3s border, 0.3s box-shadow"
   :border-radius (sizes 1)})

(defn icon []
  {:font-size     (sizes 4)
   :color         (:secondary colors)
   :font-weight   "bold"
   :font-family   (:headers fonts)
   :margin-bottom (sizes 0.5)})
