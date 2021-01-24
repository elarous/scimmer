(ns scimmer.mapping.views
  (:require ["react" :as re]
            [reagent.core :as reagent]
            [re-frame.core :as rf]
            [herb.core :refer [<class]]
            ["grommet" :refer [Grommet Button Heading Select Grid Box TextInput FormField]]
            [scimmer.mapping.subs]
            [scimmer.mapping.events]
            [scimmer.mapping.styles :as stl]
            [scimmer.mapping.card.views :refer [card header]]
            [scimmer.mapping.attribute.views :refer [attribute single-attr object-attr object-subattr array-attr array-attr-item]]))

;; helper functions
(defn get-entity-mapping [ns-k]
  (let [mapping (namespace ns-k)
        entity (name ns-k)]
    {:mapping mapping :entity entity}))

(defn get-mapping-attr-item [schema]
  (->> schema
       :children
       (some (fn [v] (and (= (first v) :value) v)))
       second
       :scimmer.services.schema/mapping))
;;

(defn schema-card []
  (let [single-attrs @(rf/subscribe [:mapping/single-attrs])
        map-attrs @(rf/subscribe [:mapping/map-attrs])
        array-attrs @(rf/subscribe [:mapping/array-attrs])]

    [card {:class (<class stl/schema-card)}
     [header "Schema" "User"]

     (for [[attr-name attr-props _schema] single-attrs]
       (let [value (get-entity-mapping (:scimmer.services.schema/mapping attr-props))]
         ^{:keys attr-name}
         [attribute attr-name
          [single-attr
           {:value value
            :on-change
                   (fn [source e]
                     (rf/dispatch [:mapping/>update-single-attr
                                   {:name    attr-name
                                    :entity  (if (= source :entity)
                                               (-> e .-target .-value)
                                               (:entity value))
                                    :mapping (if (= source :mapping)
                                               (-> e .-target .-value)
                                               (:mapping value))}]))}]]))

     (for [[attr-name _attr-props schema] map-attrs]
       [attribute attr-name
        [object-attr
         (for [[attr-name attr-props _schema] (:children schema)]
           ^{:keys attr-name}
           [object-subattr attr-name {:value (get-entity-mapping (:scimmer.services.schema/mapping attr-props))}])]])

     (for [[attr-name _attr-props schema] array-attrs]
       [attribute attr-name
        (let [arr-schema (-> schema :children first :children)]
          [array-attr
           (for [[attr-name _props children] arr-schema]
             ^{:keys attr-name}
             [array-attr-item {:value (merge {:type attr-name}
                                             (-> (get-mapping-attr-item children)
                                                 get-entity-mapping))}])])])]))

(defn mapping-page []
  [:div {:class (<class stl/container)}
   [:> Grommet {:theme (clj->js stl/grommet-theme)
                :class (<class stl/grommet)}
    [:div {:class (<class stl/grid)}
     [schema-card]
     [card {} [header "Resource" "User"]]
     [card {} [header "Entities" "User"]]]]])

