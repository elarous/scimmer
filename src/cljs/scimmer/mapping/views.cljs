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
         ^{:key attr-name}
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
       ^{:key attr-name}
       [attribute attr-name
        [object-attr
         (for [[subattr-name attr-props _schema] (:children schema)]
           (let [value (get-entity-mapping (:scimmer.services.schema/mapping attr-props))]
             ^{:key (str attr-name "_" subattr-name)}
             [object-subattr
              subattr-name
              {:value     value
               :attr-name attr-name
               :on-change (fn [source e]
                            (rf/dispatch [:mapping/>update-map-attr
                                          {:name    attr-name
                                           :subattr subattr-name
                                           :entity  (if (= source :entity)
                                                      (-> e .-target .-value)
                                                      (:entity value))
                                           :mapping (if (= source :mapping)
                                                      (-> e .-target .-value)
                                                      (:mapping value))}]))}]))]])


     (for [[attr-name _attr-props schema] array-attrs]
       ^{:key (str attr-name (:type schema))}
       [attribute attr-name
        (let [arr-schema (-> schema :children first :children)]
          [array-attr
           (for [[i [type _props children]] (doall (map-indexed vector arr-schema))]
             (let [value (merge {:type type}
                                (-> (get-mapping-attr-item children)
                                    get-entity-mapping))]
               ^{:key type}
               [array-attr-item
                {:value value
                 :on-change (fn [source e]
                              (js/console.log "source " source " e: " e)
                              (rf/dispatch [:mapping/>update-array-attr
                                            {:name   attr-name
                                             :idx    i
                                             :entity (if (= source :entity)
                                                       (-> e .-target .-value)
                                                       (:entity value))
                                             :mapping (if (= source :mapping)
                                                        (-> e .-target .-value)
                                                        (:mapping value))
                                             :type (if (= source :type)
                                                     (-> e .-target .-value keyword)
                                                     (:type value))}]))}]))])])]))

(defn resource-card []
  [card {}
   [header "Resource" "User"]
   [:textarea {:value "hi"}]])

(defn entities-card []
  [card {} [header "Entities" "User"]])

(defn mapping-page []
  [:div {:class (<class stl/container)}
   [:> Grommet {:theme (clj->js stl/grommet-theme)
                :class (<class stl/grommet)}
    [:div {:class (<class stl/grid)}
     ^{:key "schema-card"}
     [schema-card]
     ^{:key "resource-card"}
     [resource-card]
     ^{:key "entities-card"}
     [entities-card]]]])


