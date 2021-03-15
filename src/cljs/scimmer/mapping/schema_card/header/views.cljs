(ns scimmer.mapping.schema-card.header.views
  (:require ["react" :as re]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [herb.core :refer [<class]]
            ["grommet" :refer [Anchor Select TextInput Button Link]]
            ["grommet-icons" :refer [Add Trash]]
            [scimmer.theme :refer [colors]]
            [scimmer.mapping.card.views :as cv]
            [scimmer.mapping.schema-card.header.styles :as stl]
            [scimmer.mapping.schema-card.header.events]))

(defn header []
  (let [schema        @(rf/subscribe [:mapping/schema])
        schemas       @(rf/subscribe [:mapping/schemas])
        schema-saved? @(rf/subscribe [:mapping/schema-saved?])
        sch-in-schs   (some #(and (= (:id %) (:id schema)) %) schemas)]
    [cv/header
     [:div {:class (<class stl/header-container)}
      [:> Select {:placeholder "Select a schema"
                  :options     (or schemas [])
                  :value-label (if schema
                                 (r/as-element
                                  [:div {:class (<class stl/search-input)}
                                   (:name sch-in-schs)])
                                 js/undefined)
                  :disabled    (not schema-saved?)
                  :value-key   (if schema (:id schema) js/undefined)
                  :on-change   #(rf/dispatch [:mapping/>load-schema! (-> % .-option .-id)])
                  :plain       true}
       (fn [schema-item]
         (r/as-element
          [:div {:class (<class stl/search-item)}
           (aget schema-item "name")]))]
      [:> TextInput {:plain     true
                     :value     (:name schema)
                     :on-change #(rf/dispatch [:mapping/>set-schema-name (-> % .-target .-value)])}]
      (when schema-saved?
        [:> Anchor {:on-click #(rf/dispatch [:mapping/>new-schema])
                    :margin   "xsmall"
                    :color    (:primary colors)
                    :size     "small"
                    :icon     (r/create-element Add #js {:size "small"})
                    :label    "New"}])
      [:> Anchor {:on-click #(rf/dispatch [:mapping/>remove (:id schema)])
                  :margin   "xsmall"
                  :color    (:secondary colors)
                  :size     "small"
                  :icon     (r/create-element Trash #js {:size "small"})
                  :label    "Remove"}]]]))

