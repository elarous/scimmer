(ns scimmer.mapping.schema-card.array-attrs-section.views
  (:require ["react" :as re]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [herb.core :refer [<class]]
            ["grommet" :refer [Anchor Grommet Button Heading Select Grid Box TextInput FormField Layer]]
            ["grommet-icons" :refer [Add]]
            [scimmer.utils :refer [debounce]]
            [scimmer.theme :refer [colors]]
            [scimmer.mapping.schema-card.array-attrs-section.subs]
            [scimmer.mapping.events]
            [scimmer.mapping.styles :as stl]
            [scimmer.mapping.card.views :refer [card header]]
            [scimmer.mapping.schema-card.attribute.views :refer [attribute]]
            [scimmer.mapping.schema-card.array-attr.views :refer [array-attr array-attr-item]]
            [scimmer.mapping.schema-card.utils :refer [get-entity-mapping get-mapping-attr-item]]))

(defn arrays-section [{:keys [set-attr remove-attr]}]
  (let [array-attrs @(rf/subscribe [:mapping/array-attrs])]
    [:<>
     (for [{:keys [id name sub-items]} array-attrs]
       ^{:key id}
       [attribute name {:on-change #(set-attr id (-> % .-target .-value))
                        :on-remove #(remove-attr id)}
        [array-attr
         {:on-add #(rf/dispatch [:mapping/>add-sub-item id])}
         (for [{sub-item-id :id mapped-to :mapped-to type :type collection :collection} sub-items]
           ^{:key sub-item-id}
           [array-attr-item
            {:collection               collection
             :type                type
             :mapped-to           mapped-to
             :on-remove           #(rf/dispatch [:mapping/>remove-sub-item id sub-item-id])
             :on-type-change      #(rf/dispatch [:mapping/>set-array-type id sub-item-id (-> % .-target .-value)])
             :on-collection-change     #(rf/dispatch [:mapping/>set-array-collection id sub-item-id (-> % .-target .-value)])
             :on-mapped-to-change #(rf/dispatch [:mapping/>set-array-mapped-to id sub-item-id (-> % .-target .-value)])}])]])]))

