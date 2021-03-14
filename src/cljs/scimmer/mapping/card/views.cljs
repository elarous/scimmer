(ns scimmer.mapping.card.views
  (:require [reagent.core :as reagent]
            [herb.core :refer [<class join]]
            ["grommet" :refer [Grommet Button Heading Select Grid Box TextInput FormField]]
            [scimmer.mapping.card.styles :as stl]))

(defn header
  ([children]
   [:div {:class (<class stl/header)} children])
  ([card-name card-object]
   [:div {:class (<class stl/header)}
    [:div {:class (<class stl/header-container)}
     [:div {:class (<class stl/header-name)} card-name]
     [:div {:class (<class stl/header-of)} "of"]
     [:div {:class (<class stl/header-object)} card-object]]]))

(defn card [{:keys [class body-class]} header & body]
  [:div {:class (join (<class stl/card) class)}
   header
   [:div {:class (join (<class stl/body) body-class)} body]])

