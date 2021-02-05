(ns scimmer.mapping.schema-card.extensions.extension.styles
  (:require [scimmer.theme :refer [colors fonts sizes]]))

(defn header []
  {:display         "flex"
   :justify-content "space-between"
   :align-items     "center"})

(defn down-icon [collapsed?]
  {:height    (sizes 2)
   :width     (sizes 2)
   :transform (if collapsed? "rotate(-90deg)" "rotate(0deg)")})


(defn trash-icon []
  {:height (sizes 2)
   :width  (sizes 2)})

(defn label []
  ^{:pseudo {:hover {:border (str "1px solid " (:grey-3 colors))}
             :focus {:border     (str "1px solid " (:grey-3 colors))
                     :background (:grey-1 colors)}}}
  {:text-align    "center"
   :font-size     (sizes 2.5)
   :font-family   (:main fonts)
   :color         (:tertiary colors)
   :border        "1px solid transparent"
   :border-radius (sizes 0.5)
   :transition    "0.3s border, 0.3s background"
   :padding       (sizes 0.5)
   :max-width     (sizes 20)
   :outline       "none"})
