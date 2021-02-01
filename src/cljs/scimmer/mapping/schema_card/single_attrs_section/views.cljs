(ns scimmer.mapping.schema-card.single-attrs-section.views
  (:require ["react" :as re]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [herb.core :refer [<class]]
            ["grommet" :refer [Anchor Grommet Button Heading Select Grid Box TextInput FormField Layer]]
            ["grommet-icons" :refer [Add]]
            [scimmer.mapping.schema-card.single-attrs-section.subs]
            [scimmer.mapping.events]
            [scimmer.mapping.card.views :refer [card header]]
            [scimmer.mapping.schema-card.attribute.views :refer [attribute]]
            [scimmer.mapping.schema-card.single-attrs-section.single-attr.views :refer [single-attr]]
            [scimmer.mapping.schema-card.utils :refer [get-entity-mapping get-mapping-attr-item]]))

(defn singles-section [{:keys [set-attr remove-attr]}]
  (let [single-attrs @(rf/subscribe [:mapping/single-attrs])]
    [:<>
     (for [{:keys [id name mapped-to group]} single-attrs]
       ^{:key id}
       [attribute name {:on-change #(set-attr id (-> % .-target .-value))
                        :on-remove #(remove-attr id)}
        [single-attr
         {:group     group
          :mapped-to mapped-to
          :on-change
                     (fn [source e]
                       (rf/dispatch [:mapping/>update-single-attr
                                     {:name    name
                                      :entity  (if (= source :group)
                                                 (-> e .-target .-value)
                                                 group)
                                      :mapping (if (= source :mapped-to)
                                                 (-> e .-target .-value)
                                                 mapped-to)}]))}]])]))

