(ns scimmer.mapping.styles
  (:require [scimmer.theme :refer [colors fonts sizes shadows]]
            [reagent.core :as r]))

;; Grommet theme
(def grommet-theme
  {:global    {:font {:family (:normal fonts)
                      :size   (sizes 2)
                      :height (sizes 2)}}
   :formField {:border {:position "none"}}
   :select    {:background (:grey-2 colors)}})

(defn container []
  {:padding  (sizes 3)
   :overflow "hidden"})

(defn grommet []
  {:height "100%"})

(defn grid []
  {:display               "grid"
   :height                "100%"
   :grid-gap              (sizes 2)
   :grid-template-columns "50% 50%"
   :grid-template-rows    "50% 50%"})


