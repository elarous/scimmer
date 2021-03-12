(ns scimmer.mapping.schema-card.header.views
  (:require ["react" :as re]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [herb.core :refer [<class]]
            ["grommet" :refer [Select]]
            [scimmer.mapping.card.views :as cv]
            [scimmer.mapping.schema-card.header.styles :as stl]))

(defn header []
  (let [schema  @(rf/subscribe [:mapping/schema])
        schemas @(rf/subscribe [:mapping/schemas])]
    [cv/header
     [:> Select {:placeholder "Select a schema"
                 :options     (or schemas [])
                 :value-label (when schema
                                (r/as-element
                                 [:div {:class (<class stl/search-input)}
                                  (:name schema)]))
                 :value-key   (:id schema)
                 :on-change   #(rf/dispatch [:mapping/>load-schema! (-> % .-option .-id)])
                 :plain       true}
      (fn [schema-item]
        (r/as-element
         [:div {:class (<class stl/search-item)}
          (aget schema-item "name")]))]]))

