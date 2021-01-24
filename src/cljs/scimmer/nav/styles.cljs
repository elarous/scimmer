(ns scimmer.nav.styles
  (:require [scimmer.theme :refer [colors sizes fonts shadows]]))

(defn nav-style []
  {:display                    "flex"
   :flex-direction             "column"
   :justify-content            "space-between"
   :height                     "100%"
   :margin                     "0"
   :box-shadow                 (:medium shadows)
   :border-bottom-right-radius (sizes 4)
   :border-top-right-radius    (sizes 4)
   :background                 (:bg-dark colors)})

(defn nav-logo-style []
  {:display         "flex"
   :justify-content "center"
   :align-items     "center"
   :font-size       (sizes 5)
   :font-family     (:logo fonts)
   :margin-top      (sizes 2)
   :color           (:highlight colors)})

(defn nav-main-links []
  {:margin-top (sizes 20)})

;; Link styles
(defn link-style []
  {:display "flex"})

(defn active-link-indicator [active?]
  {:background                 (:highlight colors)
   :border-bottom-right-radius (sizes 1)
   :border-top-right-radius    (sizes 1)
   :opacity                    (if active? 1 0)
   :width                      (sizes 0.5)
   :height                     (sizes 4)
   :transition                 "0.3s opacity"})

(defn icon-style []
  {:margin-left (sizes 3)})

(defn icon-path-style [active?]
  {:fill       (if active? (:highlight colors) (:grey-1 colors))
   :transition "0.3s fill"})

(defn link-title-style [active?]
  {:color       (if active? (:highlight colors) (:grey-1 colors))
   :margin-left (sizes 3)
   :font-size   (sizes 2.5)
   :font-family (:headers fonts)
   :transition  "0.3s color"})

(defn footer-style []
  {:margin-bottom (sizes 10)})
