(ns scimmer.mapping.schema-card.add-attr.views
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
            [scimmer.mapping.schema-card.add-attr.styles :as stl]))



(defn add-attr-modal [{:keys [on-close]}]
  [:> Layer {:on-esc           on-close
             :on-click-outside on-close}
   [:div {:class (<class stl/add-modal)}
    [:h2 {:class (<class stl/add-modal-title)} "Which kind of attribute?"]
    [:div {:class (<class stl/buttons)}
     [:button {:class    (<class stl/button)
               :on-click (fn [_]
                           (rf/dispatch [:mapping/>add-single-attr])
                           (on-close))}
      [:div {:class (<class stl/icon)} "1"] "Single"]
     [:button {:class    (<class stl/button)
               :on-click (fn [_]
                           (rf/dispatch [:mapping/>add-map-attr])
                           (on-close))}
      [:div {:class (<class stl/icon)} "{}"] "Object"]
     [:button {:class    (<class stl/button)
               :on-click (fn [_]
                           (rf/dispatch [:mapping/>add-array-attr])
                           (on-close))}
      [:div {:class (<class stl/icon)} "[]"] "Array"]]]])
