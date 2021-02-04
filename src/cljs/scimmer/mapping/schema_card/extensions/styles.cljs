(ns scimmer.mapping.schema-card.extensions.styles
  (:require [scimmer.theme :refer [sizes colors fonts]]))

(defn extensions []
  {:flex 1})

(defn header []
  {:font-family   (:header fonts)
   :font-size     (sizes 3)
   :font-weight   "bold"
   :margin-bottom (sizes 5)
   :color         (:primary colors)
   :padding-left  (sizes 2)
   :border-bottom (str "1px solid " (:grey-3 colors))})


