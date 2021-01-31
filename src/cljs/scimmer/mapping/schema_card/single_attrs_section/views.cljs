(ns scimmer.mapping.schema-card.single-attrs-section.views
  (:require ["react" :as re]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [herb.core :refer [<class]]
            ["grommet" :refer [Anchor Grommet Button Heading Select Grid Box TextInput FormField Layer]]
            ["grommet-icons" :refer [Add]]
            [scimmer.mapping.subs]
            [scimmer.mapping.events]
            [scimmer.mapping.card.views :refer [card header]]
            [scimmer.mapping.schema-card.attribute.views :refer [attribute]]
            [scimmer.mapping.schema-card.single-attrs-section.single-attr.views :refer [single-attr]]
            [scimmer.mapping.schema-card.utils :refer [get-entity-mapping get-mapping-attr-item]]))

(defn singles-section [{:keys [set-attr remove-attr]}]
  (let [single-attrs @(rf/subscribe [:mapping/single-attrs])]
    [:<>
     (for [item single-attrs]
       (let [[attr-name attr-props _schema] item
             value (get-entity-mapping (:scimmer.services.schema/mapping attr-props))]
         ^{:key (-> item meta :id)}
         [attribute attr-name {:on-change #(set-attr (-> item meta :id)
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
                                               (:mapping value))}]))}]]))]))