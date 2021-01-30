(ns scimmer.mapping.styles
  (:require [scimmer.theme :refer [colors fonts sizes shadows]]))

;; Grommet theme
(def grommet-theme
  {:global    {:font {:family (:normal fonts)
                      :size   (sizes 2)
                      :height (sizes 2)}}
   :formField {:border {:position "none"}}})

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

(defn schema-card []
  {:grid-row "1/3"})

(defn resource-card-body []
  {:overflow "hidden"})

(defn resource-textarea []
  {:width                      "100%"
   :height                     "100%"
   :margin                     0
   :padding                    (sizes 1)
   :box-sizing                 "border-box"
   :color                      (:text-secondary colors)
   :font-size                  (sizes 2)
   :font-family                (:code fonts)
   :outline                    "none"
   :resize                     "none"
   :border-bottom-right-radius (sizes 1)
   :border-bottom-left-radius  (sizes 1)
   :border                     "none"})

(defn entities []
  {:height      "100%"
   :font-size   (sizes 2)
   :font-family (:code fonts)
   :overflow    "auto"})

;;
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

(defn top-actions []
  {:display         "flex"
   :justify-content "flex-end"
   :align-items     "center"
   :margin          (sizes 1)})
