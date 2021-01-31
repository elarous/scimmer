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
               on-close #(reset! attr-modal-visible? false)]
    [:div
     (when @attr-modal-visible?
       [add-attr-modal {:on-close on-close}])
     [:div {:class (<class stl/top-actions)}
      [:> Anchor {:on-click #(reset! attr-modal-visible? true)
                  :margin   "xsmall"
                  :color    (:secondary colors)
                  :size     "small"
                  :icon     (r/create-element Add #js {:size "small"})
                  :label    "Add attribute"}]]]))
