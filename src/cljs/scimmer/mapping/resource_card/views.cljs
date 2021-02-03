(ns scimmer.mapping.resource-card.views
  (:require ["react" :as re]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [herb.core :refer [<class]]
            ["grommet" :refer [Anchor Grommet Button Heading Select Grid Box TextInput FormField Layer]]
            ["grommet-icons" :refer [Add]]
            ["react-json-view" :as ReactJson]
            [scimmer.utils :refer [debounce]]
            [scimmer.theme :refer [colors]]
            [scimmer.mapping.resource-card.events]
            [scimmer.mapping.resource-card.styles :as stl]
            [scimmer.mapping.card.views :refer [card header]]
            [scimmer.mapping.schema-card.single-attrs-section.views :refer [singles-section]]
            [scimmer.mapping.schema-card.object-attrs-section.views :refer [objects-section]]
            [scimmer.mapping.schema-card.array-attrs-section.views :refer [arrays-section]]
            [scimmer.mapping.schema-card.top-actions.views :refer [top-actions]]
            [scimmer.mapping.schema-card.utils :refer [get-entity-mapping get-mapping-attr-item]]
            [scimmer.theme :refer [colors fonts]]))

(def react-json (.-default ReactJson))

(defn resource-card []
  (r/with-let [contents (r/atom "")
               on-change (fn [obj]
                           (rf/dispatch [:mapping/>set-resource (js->clj (aget obj "updated_src")
                                                                         :keywordize-keys true)]))]
    [card {:body-class (<class stl/resource-card-body)}
     [header "Resource" "User"]
     [:div {:class (<class stl/resource-container)}
      [:> react-json {:src                 @(rf/subscribe [:mapping/resource-json])
                      :style  #js {:fontFamily (:code fonts)}
                      :display-object-size false
                      :display-data-types  false
                      :enable-clipboard    false
                      :collapsed           true
                      :on-delete           on-change
                      :on-add              on-change
                      :on-edit             on-change}]]]))

;; TODO: put this back the UI with tabulation to choose between it and the Json Editor (and pretty json)
#_[:textarea {:class         (<class stl/resource-textarea)
              :default-value @(rf/subscribe [:mapping/resource-json])
              :on-change     on-change}]
