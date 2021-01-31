(ns scimmer.mapping.schema-card.input.styles
  (:require [scimmer.theme :refer [colors sizes]]))

(defn attr-form-field []
  {:border    "none !important"
   :flex      1
   :outline   "none"
   :font-size (sizes 0.2)
   :margin    (sizes 1)})

(defn attr-input []
  {:border  (str "1px solid " (:grey-3 colors))
   :padding (sizes 1)})
