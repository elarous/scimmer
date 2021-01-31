(ns scimmer.mapping.schema-card.object-attrs-section.views
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
            [scimmer.mapping.card.views :refer [card header]]
            [scimmer.mapping.schema-card.attribute.views :refer [attribute]]
            [scimmer.mapping.schema-card.object-attrs-section.object-attr.views :refer [object-attr sub-attr object-inputs]]
            [scimmer.mapping.schema-card.utils :refer [get-entity-mapping get-mapping-attr-item]]))

(defn objects-section [{:keys [set-attr remove-attr]}]
  (let [map-attrs @(rf/subscribe [:mapping/map-attrs])]
    [:<>
     (for [item map-attrs]
       (let [[attr-name _attr-props schema] item
             set-sub-attribute (fn [attr-id sub-attr-id sub-attr]
                                 (rf/dispatch [:mapping/>set-sub-attr attr-id sub-attr-id sub-attr]))
             remove-sub-attribute (fn [attr-id sub-attr-id]
                                    (rf/dispatch [:mapping/>remove-sub-attr attr-id sub-attr-id]))]
         ^{:key (-> item meta :id)}
         [attribute attr-name {:on-change #(set-attr (-> item meta :id)
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
                                                         (:mapping value))}]))}]]))]]))]))
