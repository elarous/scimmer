(ns scimmer.mapping.views
  (:require ["react" :as re]
            [reagent.core :as reagent]
            [herb.core :refer [<class]]
            [scimmer.theme :refer [colors sizes fonts]]
            ["grommet" :refer [Grommet Button Heading Select Grid Box TextInput FormField]]))

(defn button-style []
  {:color            "pink"
   :width            "300px"
   :background-color "yellow"})

(defn heading-style []
  {:color (:text-primary colors)})

(def theme {:global    {:font {:family (:normal fonts)
                               :size   (sizes 2)
                               :height (sizes 2)}}
            :formField {:border {:position "none"}}})


;; schema components
(defn attr-header-style []
  {:margin           (sizes 1)
   :padding          (sizes 1)
   :background-color (:grey-2 colors)
   :color            (:primary colors)
   :border-radius    (sizes 1)
   :text-align       "center"
   :font-weight      "600"})

(defn attr-inputs-container-style []
  {:display          "flex"
   :background-color (:grey-1 colors)
   :border-radius    (sizes 1)
   :padding          (sizes 1)
   :margin-left      (sizes 2)
   :margin-right     (sizes 2)})

(defn attr-form-field-style []
  {:border  "none !important"
   :outline "none"
   :margin  (sizes 1)})

(defn attr-input []
  {:border  (str "1px solid " (:grey-3 colors))
   :padding (sizes 1)})

(defn attr-header [name]
  [:div
   [:> Heading {:size  "xsmall"
                :class (<class attr-header-style)} name]])

(defn input [name label]
  [:> FormField {:name     name
                 :html-for "text-input"
                 :label    label
                 :class    (<class attr-form-field-style)}
   [:> TextInput {:placeholder label
                  :size        "small"
                  :class       (<class attr-input)
                  :id          "text-input"
                  :name        name}]])

(defn entity-input [] [input "entity" "Entity"])

(defn mapping-input [] [input "mapping" "Mapping"])


(defn attr [name entity value]
  [:div
   [attr-header name]
   [:div {:class (<class attr-inputs-container-style)}
    [entity-input]
    [mapping-input]]])

(defn schema []
  [:> Box {:grid-area "schema"}
   [attr "userName" "user" "user_name"]])

;;

(defn mapping-page []
  [:div
   [:> Grommet {:theme (clj->js theme)}
    [:> Heading {:size  "small"
                 :class (<class heading-style)}
     "Set mappings from resource to your entities"]
    [:> Select {:options   ["User" "Group"]
                :value     "User"
                :on-change #(js/console.log (aget % "value"))}]
    [:> Grid {:rows    #js ["auto" "auto"]
              :columns #js ["auto" "auto"]
              :gap     "small"
              :areas   (clj->js [{:name "schema" :start [0 0] :end [1 1]}
                                 {:name "resource" :start [1 0] :end [1 1]}
                                 {:name "entities" :start [1 1] :end [1 1]}])}
     [schema]
     [:> Box {:grid-area "resource" :background "light-5"} "Resource"]
     [:> Box {:grid-area "entities" :background "light-2"} "Entities"]]]])



