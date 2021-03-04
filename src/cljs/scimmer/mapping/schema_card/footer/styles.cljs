(ns scimmer.mapping.schema-card.footer.styles
  (:require [scimmer.theme :refer [colors sizes fonts]]))

(defn footer []
  {:display                    "flex"
   :justify-content            "center"
   :border-bottom-right-radius (sizes 1)
   :border-bottom-left-radius  (sizes 1)
   :background                 (:grey-1 colors)})

(defn button []
  {:margin (sizes 1)})

(defn save-btn []
  ^{:pseudo {:active {:box-shadow "none"}
             :focus  {:box-shadow "none"}
             :hover  {:box-shadow "none"}}}
  {:background  (:primary colors)
   :color       "#fff"
   :outline     "none"
   :border      "none"
   :font-family (:main fonts)})




