(ns scimmer.mapping.views
  (:require ["react" :as re]
            [reagent.core :as reagent]
            [re-frame.core :as rf]
            [herb.core :refer [<class]]
            ["grommet" :refer [Grommet Button Heading Select Grid Box TextInput FormField]]
            [scimmer.mapping.subs]
            [scimmer.mapping.styles :as stl]
            [scimmer.mapping.card.views :refer [card header]]
            [scimmer.mapping.attribute.views :refer [attribute single-attr object-attr array-attr]]))

(defn get-entity-mapping [ns-k]
  (println ns-k)
  (let [mapping (namespace ns-k)
        entity (name ns-k)]
    {:mapping mapping :entity entity}))

(defn schema-card []
  (let [single-attrs @(rf/subscribe [:mapping/single-attrs])]
    [card {:class (<class stl/schema-card)}
     [header "Schema" "User"]
     (for [[attr-name attr-props _schema] single-attrs]
       ^{:keys attr-name}
       [attribute attr-name [single-attr {:value     (get-entity-mapping (:scimmer.services.schema/mapping attr-props))
                                          :on-change #(js/console.log (-> % .-target .-value))}]])
     [attribute "userName" [object-attr {:firstName {:value     {:entity "first_name" :mapping "profile"}
                                                     :on-change #(js/console.log (-> % .-target .-value))}
                                         :lastName  {:value     {:entity "last_name" :mapping "profile"}
                                                     :on-change #(js/console.log (-> % .-target .-value))}}]]
     [attribute "email" [array-attr {:value     [{:entity "primary_email" :type "work" :mapping "profile"}
                                                 {:entity "secondary_email" :type "mobile" :mapping "profile"}]
                                     :on-change #(js/console.log (-> % .-target .-value))}]]]))

(defn mapping-page []
  [:div {:class (<class stl/container)}
   [:> Grommet {:theme (clj->js stl/grommet-theme)
                :class (<class stl/grommet)}
    [:div {:class (<class stl/grid)}
     [schema-card]
     [card {} [header "Resource" "User"]]
     [card {} [header "Entities" "User"]]]]])

