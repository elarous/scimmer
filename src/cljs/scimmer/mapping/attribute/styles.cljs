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
   :cursor      "pointer"
   :font-weight "bold"})

(defn collapse-icon [collapsed?]
  {:transform (if collapsed? "rotate(-90deg)" "none")})


(defn body []
  {})

(defn single-attr []
  {:display         "flex"
   :justify-content "space-between"
   :align-items     "center"
   :border-radius   (sizes 1)
   :padding         (sizes 1)
   :margin-left     (sizes 2)
   :margin-right    (sizes 2)})

(defn attr-form-field []
  {:border    "none !important"
   :flex 1
   :outline   "none"
   :font-size (sizes 0.2)
   :margin    (sizes 1)})

(defn attr-input []
  {:border  (str "1px solid " (:grey-3 colors))
   :padding (sizes 1)})
