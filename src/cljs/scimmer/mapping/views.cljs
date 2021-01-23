(ns scimmer.mapping.views
  (:require ["react" :as re]
            [reagent.core :as reagent]
            [herb.core :refer [<class]]
            ["grommet" :refer [Grommet Button Heading Select Grid Box TextInput FormField]]
            [scimmer.mapping.styles :as stl]
            [scimmer.mapping.card.views :refer [card header]]))


;(defn attr-header [name]
;  [:div
;   [:> Heading {:size  "xsmall"
;                :class (<class stl/attr-header)} name]])
;
;(defn input [name label]
;  [:> FormField {:name     name
;                 :html-for "text-input"
;                 :label    label
;                 :class    (<class stl/attr-form-field)}
;   [:> TextInput {:placeholder label
;                  :size        "small"
;                  :class       (<class stl/attr-input)
;                  :id          "text-input"
;                  :name        name}]])
;
;(defn entity-input [] [input "entity" "Entity"])
;
;(defn mapping-input [] [input "mapping" "Mapping"])
;
;
;(defn attr [name entity value]
;  [:div
;   [attr-header name]
;   [:div {:class (<class stl/attr-inputs-container)}
;    [entity-input]
;    [mapping-input]]])
;
;(defn schema []
;  [:> Box {:grid-area "schema"}
;   [attr "userName" "user" "user_name"]])
;
;;;

(defn mapping-page []
  [:div {:class (<class stl/container)}
   [:> Grommet {:theme (clj->js stl/grommet-theme)
                :class (<class stl/grommet)}
    [:div {:class (<class stl/grid)}
     [card {:class (<class stl/schema-card)}
      [header "Schema" "User"]]
     [card {} [header "Resource" "User"]]
     [card {} [header "Entities" "User"]]]]])

