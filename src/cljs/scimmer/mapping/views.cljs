(ns scimmer.mapping.views
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
            [scimmer.mapping.styles :as stl]
            [scimmer.mapping.card.views :refer [card header]]
            [scimmer.mapping.attribute.views :refer [attribute sub-attr single-attr object-attr object-inputs array-attr array-attr-item]]))

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
     [:button {:class (<class stl/button)
               :on-click (fn [_]
                           (rf/dispatch [:mapping/>add-array-attr])
                           (on-close))}
      [:div {:class (<class stl/icon)} "[]"] "Array"]]]])

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

(defn schema-card []
  (let [single-attrs @(rf/subscribe [:mapping/single-attrs])
        map-attrs @(rf/subscribe [:mapping/map-attrs])
        array-attrs @(rf/subscribe [:mapping/array-attrs])
        set-attribute #(rf/dispatch [:mapping/>set-attr %1 %2])
        remove-attr #(rf/dispatch [:mapping/>remove-attr %1])]
    [card {:class (<class stl/schema-card)}
     [header "Schema" "User"]
     [top-actions]
     (for [item single-attrs]
       (let [[attr-name attr-props _schema] item
             value (get-entity-mapping (:scimmer.services.schema/mapping attr-props))]
         ^{:key (-> item meta :id)}
         [attribute attr-name {:on-change #(set-attribute (-> item meta :id)
                                                          (-> % .-target .-value))
                               :on-remove #(remove-attr (-> item meta :id))}
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
       (let [[attr-name _attr-props schema] item
             set-sub-attribute (fn [attr-id sub-attr-id sub-attr]
                                 (rf/dispatch [:mapping/>set-sub-attr attr-id sub-attr-id sub-attr]))
             remove-sub-attribute (fn [attr-id sub-attr-id]
                                    (rf/dispatch [:mapping/>remove-sub-attr attr-id sub-attr-id]))]
         ^{:key (-> item meta :id)}
         [attribute attr-name {:on-change #(set-attribute (-> item meta :id)
                                                          (-> % .-target .-value))
                               :on-remove #(remove-attr (-> item meta :id))}
          [object-attr
           (-> item meta :id)
           (for [sub-item (:children schema)]
             (let [[subattr-name attr-props _schema] sub-item
                   value (get-entity-mapping (:scimmer.services.schema/mapping attr-props))]
               ^{:key (-> sub-item meta :id)}
               [sub-attr subattr-name {:on-change #(set-sub-attribute (-> item meta :id)
                                                                      (-> sub-item meta :id)
                                                                      (-> % .-target .-value))
                                       :on-remove #(remove-sub-attribute (-> item meta :id)
                                                                         (-> sub-item meta :id))}
                [object-inputs
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
                                                         (:mapping value))}]))}]]))]]))

     (for [item array-attrs]
       (let [[attr-name _attr-props schema] item
             remove-sub-item #(rf/dispatch [:mapping/>remove-sub-item %1 %2])]
         ^{:key (-> item meta :id)}
         [attribute attr-name {:on-change #(set-attribute (-> item meta :id)
                                                          (-> % .-target .-value))
                               :on-remove #(remove-attr (-> item meta :id))}
          (let [arr-schema (-> schema :children first :children)]
            [array-attr
             (-> item meta :id)
             (for [[i [type _props children]] (doall (map-indexed vector arr-schema))]
               (let [value (merge {:type type}
                                  (-> (get-mapping-attr-item children)
                                      get-entity-mapping))]
                 ^{:key i}
                 [array-attr-item
                  {:value     value
                   :on-remove #(remove-sub-item (-> item meta :id) type)
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


