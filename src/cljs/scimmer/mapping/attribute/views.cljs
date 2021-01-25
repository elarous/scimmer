(ns scimmer.mapping.attribute.views
  (:require [reagent.core :as r]
            [herb.core :refer [<class join]]
            ["grommet" :refer [Grommet Button Heading Select Grid Box TextInput FormField]]
            [scimmer.mapping.attribute.styles :as stl]))

;; Icons
(defn collapse-icon [{:keys [collapsed?]}]
  [:svg {:class (<class stl/collapse-icon collapsed?) :xmlns "http://www.w3.org/2000/svg", :width "24", :height "24", :viewbox "0 0 24 24"}
   [:g {:id "arrow-right", :transform "translate(12 12) rotate(90)"}
    [:g {:id "arrow-right-2", :data-name "arrow-right", :transform "translate(-12 -12)"}
     [:g {:id "arrow-right-3", :data-name "arrow-right"}
      [:rect {:id "Rectangle_39", :data-name "Rectangle 39", :width "24", :height "24", :transform "translate(24 24) rotate(180)", :fill "#595f72", :opacity "0"}]
      [:path {:id "Path_107", :data-name "Path 107", :d "M10.46,18a2.23,2.23,0,0,1-.91-.2A1.76,1.76,0,0,1,8.5,16.21V7.79A1.76,1.76,0,0,1,9.55,6.2a2.1,2.1,0,0,1,2.21.26l5.1,4.21a1.7,1.7,0,0,1,0,2.66l-5.1,4.21A2.06,2.06,0,0,1,10.46,18Z", :fill "#595f72"}]]]]])

(defn reset-icon []
  [:svg {:xmlns "http://www.w3.org/2000/svg", :width "24", :height "24", :viewbox "0 0 24 24"}
   [:g {:id "refresh-outline", :transform "translate(12 12)"}
    [:g {:id "refresh-outline-2", :data-name "refresh-outline", :transform "translate(-12 -12)"}
     [:g {:id "refresh"}
      [:rect {:id "Rectangle_35", :data-name "Rectangle 35", :width "24", :height "24", :fill "#97a0be", :opacity "0"}]
      [:path {:id "Path_103", :data-name "Path 103", :d "M20.3,13.43a1,1,0,0,0-1.25.65A7.14,7.14,0,0,1,12.18,19,7.1,7.1,0,0,1,5,12a7.1,7.1,0,0,1,7.18-7,7.26,7.26,0,0,1,4.65,1.67l-2.17-.36a1,1,0,1,0-.32,1.98l4.24.7h.17a1,1,0,0,0,.34-.06.33.33,0,0,0,.1-.06.78.78,0,0,0,.2-.11l.09-.11c0-.05.09-.09.13-.15s0-.1.05-.14a1.34,1.34,0,0,0,.07-.18l.75-4a1.018,1.018,0,0,0-2-.38l-.27,1.45A9.21,9.21,0,0,0,12.18,3,9.1,9.1,0,0,0,3,12a9.1,9.1,0,0,0,9.18,9A9.12,9.12,0,0,0,21,14.68a1,1,0,0,0-.7-1.25Z", :fill "#97a0be"}]]]]])
;;

(defn input [{:keys [name label value on-change]}]
  [:> FormField {:name     name
                 :class    (<class stl/attr-form-field)}
   [:> TextInput {:placeholder label
                  :size        "small"
                  :class       (<class stl/attr-input)
                  :name        name
                  :value       value
                  :on-change   on-change}]])

(defn single-attr [{:keys [value on-change]}]
  [:div {:class (<class stl/single-attr)}
   [input {:name        "entity"
           :placeholder "Entity"
           :value       (:entity value)
           :on-change   (partial on-change :entity)}]
   [input {:name        "mapping"
           :placeholder "Mapping"
           :value       (:mapping value)
           :on-change   (partial on-change :mapping)}]
   [reset-icon]])

(defn object-subattr [subattr {:keys [value on-change]}]
  [:div {:class (<class stl/object-subattr)}
   [:div {:class (<class stl/object-attr-title)} (name subattr)]
   [:div {:class (<class stl/object-subattr-inputs)}
    [input {:name        "entity"
            :placeholder "Entity"
            :value       (:entity value)
            :on-change   (partial on-change :entity)}]
    [input {:name        "mapping"
            :placeholder "Mapping"
            :value       (:mapping value)
            :on-change   (partial on-change :mapping)}]
    [reset-icon]]])

(defn object-attr [& body]
  [:div {:class (<class stl/object-attr)} body])

(defn array-attr-item [{:keys [value on-change]}]
  (let [{:keys [entity type mapping]} value]
    [:div {:class (<class stl/array-attr-inputs)}
     [input {:name        "entity"
             :placeholder "Entity"
             :value       entity
             :on-change   (partial on-change :entity)}]
     [input {:name        "type"
             :placeholder "Type"
             :value       type
             :on-change   (partial on-change :type)}]
     [input {:name        "mapping"
             :placeholder "Mapping"
             :value       mapping
             :on-change   (partial on-change :mapping)}]
     [reset-icon]]))

(defn array-attr [& body]
  [:div {:class (<class stl/array-attr)} body])


(defn attribute [name body]
  (r/with-let [collapsed? (r/atom false)]
    [:div {:class (<class stl/container)}
     [:div {:class    (<class stl/head)
            :on-click #(swap! collapsed? not)}
      [collapse-icon {:collapsed? @collapsed?}]
      [:div {} name]]
     (when-not @collapsed?
       [:div {:class (<class stl/body)} body])]))

