(ns scimmer.mapping.schema-card.extensions.views
  (:require [reagent.core :as re]
            [re-frame.core :as rf]
            [herb.core :refer [<class]]
            [scimmer.mapping.schema-card.extensions.subs]
            [scimmer.mapping.schema-card.extensions.styles :as stl]
            [scimmer.mapping.schema-card.extensions.extension.views :refer [extension]]))

(defn extensions []
  [:div {:class (<class stl/extensions)}
   [:div {:class (<class stl/header)} "Extensions"]
   (for [ext @(rf/subscribe [:mapping/extensions])]
     ^{:key (:id ext)}
     [extension ext])])
