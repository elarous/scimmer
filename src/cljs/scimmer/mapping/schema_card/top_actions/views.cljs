(ns scimmer.mapping.schema-card.top-actions.views
  (:require ["react" :as re]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [herb.core :refer [<class]]
            ["grommet" :refer [Anchor Grommet Button Heading Select Grid Box TextInput FormField Layer]]
            ["grommet-icons" :refer [Add]]
            [scimmer.utils :refer [debounce]]
            [scimmer.theme :refer [colors]]
            [scimmer.mapping.subs]
            [scimmer.mapping.events]
            [scimmer.mapping.schema-card.top-actions.styles :as stl]
            [scimmer.mapping.card.views :refer [card header]]
            [scimmer.mapping.schema-card.add-attr.views :refer [add-attr-modal]]
            [scimmer.mapping.schema-card.utils :refer [get-entity-mapping get-mapping-attr-item]]))

(defn top-actions []
  (r/with-let [attr-modal-visible? (r/atom false)
               on-close #(reset! attr-modal-visible? false)
               on-add-single (fn [_]
                               (rf/dispatch [:mapping/>add-single-attr])
                               (on-close))
               on-add-object (fn [_]
                               (rf/dispatch [:mapping/>add-object-attr])
                               (on-close))
               on-add-array (fn [_]
                              (rf/dispatch [:mapping/>add-array-attr])
                              (on-close))]
    [:div
     (when @attr-modal-visible?
       [add-attr-modal {:on-close on-close :on-add-single on-add-single :on-add-object on-add-object :on-add-array on-add-array}])
     [:div {:class (<class stl/top-actions)}
      [:> Anchor {:on-click #(reset! attr-modal-visible? true)
                  :margin   "xsmall"
                  :color    (:secondary colors)
                  :size     "small"
                  :icon     (r/create-element Add #js {:size "small"})
                  :label    "Add attribute"}]]]))

(defn top-actions-ext [ext-id]
  (r/with-let [attr-modal-visible? (r/atom false)
               on-close #(reset! attr-modal-visible? false)
               on-add-single (fn [_]
                               (rf/dispatch [:mapping/>add-ext-single-attr ext-id])
                               (on-close))
               on-add-object (fn [_]
                               (rf/dispatch [:mapping/>add-ext-object-attr ext-id])
                               (on-close))
               on-add-array (fn [_]
                              (rf/dispatch [:mapping/>add-ext-array-attr ext-id])
                              (on-close))]
    [:div
     (when @attr-modal-visible?
       [add-attr-modal {:on-close on-close :on-add-single on-add-single :on-add-object on-add-object :on-add-array on-add-array}])
     [:div {:class (<class stl/top-actions)}
      [:> Anchor {:on-click #(reset! attr-modal-visible? true)
                  :margin   "xsmall"
                  :color    (:secondary colors)
                  :size     "small"
                  :icon     (r/create-element Add #js {:size "small"})
                  :label    "Add attribute"}]]]))
