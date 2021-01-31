(ns scimmer.mapping.schema-card.array-attrs-section.views
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
            [scimmer.mapping.schema-card.attribute.views :refer [attribute]]
            [scimmer.mapping.schema-card.array-attrs-section.array-attr.views :refer [array-attr array-attr-item]]
            [scimmer.mapping.schema-card.utils :refer [get-entity-mapping get-mapping-attr-item]]))

(defn arrays-section [{:keys [set-attr remove-attr]}]
  (let [array-attrs @(rf/subscribe [:mapping/array-attrs])]
    [:<>
     (for [item array-attrs]
       (let [[attr-name _attr-props schema] item
             remove-sub-item #(rf/dispatch [:mapping/>remove-sub-item %1 %2])]
         ^{:key (-> item meta :id)}
         [attribute attr-name {:on-change #(set-attr (-> item meta :id)
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