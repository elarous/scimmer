(ns scimmer.mapping.views
  (:require ["react" :as re]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [herb.core :refer [<class]]
            ["grommet" :refer [Grommet Button Heading Select Grid Box TextInput FormField]]
            [scimmer.utils :refer [debounce]]
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
        array-attrs @(rf/subscribe [:mapping/array-attrs])
        set-attribute #(rf/dispatch [:mapping/>set-attr %1 %2])]
    [card {:class (<class stl/schema-card)}
     [header "Schema" "User"]

     (for [item single-attrs]
       (let [[attr-name attr-props _schema] item
             value (get-entity-mapping (:scimmer.services.schema/mapping attr-props))]
         ^{:key (-> item meta :id)}
         [attribute attr-name {:on-change #(set-attribute (-> item meta :id)
                                                          (-> % .-target .-value))}
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

     (for [item map-attrs]
       (let [[attr-name _attr-props schema] item]
         ^{:key (-> item meta :id)}
         [attribute attr-name {:on-change #(set-attribute (-> item meta :id)
                                                          (-> % .-target .-value))}
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
                                                        (:mapping value))}]))}]))]]))


     (for [item array-attrs]
       (let [[attr-name _attr-props schema] item]
         ^{:key (-> item meta :id)}
         [attribute attr-name {:on-change #(set-attribute (-> item meta :id)
                                                          (-> % .-target .-value))}
          (let [arr-schema (-> schema :children first :children)]
            [array-attr
             (for [[i [type _props children]] (doall (map-indexed vector arr-schema))]
               (let [value (merge {:type type}
                                  (-> (get-mapping-attr-item children)
                                      get-entity-mapping))]
                 ^{:key i}
                 [array-attr-item
                  {:value     value
                   :on-change (fn [source e]
                                (rf/dispatch [:mapping/>update-array-attr
                                              {:name    attr-name
                                               :idx     i
                                               :entity  (if (= source :entity)
                                                          (-> e .-target .-value)
                                                          (:entity value))
                                               :mapping (if (= source :mapping)
                                                          (-> e .-target .-value)
                                                          (:mapping value))
                                               :type    (if (= source :type)
                                                          (-> e .-target .-value keyword)
                                                          (:type value))}]))}]))])]))]))

(defn resource-card []
  (r/with-let [contents (r/atom "")
               on-change (debounce
                           #(reset! contents (-> % .-target .-value))
                           #(rf/dispatch [:mapping/>set-resource @contents])
                           500)]
    [card {:body-class (<class stl/resource-card-body)}
     [header "Resource" "User"]
     [:textarea {:class     (<class stl/resource-textarea)
                 :default-value
                            (.stringify js/JSON (clj->js @(rf/subscribe [:mapping/resource])) nil 2)
                 :on-change on-change}]]))


(defn entities-card []
  [card {}
   [header "Entities" "User"]
   [:pre {:class (<class stl/entities)}
    (.stringify js/JSON (clj->js @(rf/subscribe [:mapping/entities])) nil 2)]])


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


