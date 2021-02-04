(ns scimmer.mapping.schema-card.extensions.extension.views
  (:require [re-frame.core :as rf]
            [herb.core :refer [<class]]
            [scimmer.mapping.schema-card.extensions.extension.styles :as stl]
            [scimmer.mapping.schema-card.attribute.views :refer [attribute]]
            [scimmer.mapping.schema-card.single-attr.views :refer [single-attr]]
            [scimmer.mapping.schema-card.object-attr.views :refer [object-attr sub-attr object-inputs]]
            [scimmer.mapping.schema-card.array-attr.views :refer [array-attr array-attr-item]]))

(defn extension [ext]
  [:div
   [:div {:class (<class stl/label)} (:label ext)]
   (let [singles @(rf/subscribe [:mapping/ext-singles (:id ext)])
         objects @(rf/subscribe [:mapping/ext-objects (:id ext)])
         arrays @(rf/subscribe [:mapping/ext-arrays (:id ext)])]
     (concat
       (for [{:keys [id name group mapped-to]} singles]
         [attribute name {}
          [single-attr {:group               group
                        :mapped-to           mapped-to
                        :on-group-change     #(rf/dispatch [:mapping/>set-single-group id % (:id ext)])
                        :on-mapped-to-change #(rf/dispatch [:mapping/>set-single-mapped-to id % (:id ext)])}]])
       (for [{:keys [id name sub-attrs]} objects]
         ^{:key id}
         [attribute name {}
          [object-attr
           {}
           (for [{sub-attr-id :id sub-name :name mapped-to :mapped-to group :group} sub-attrs]
             ^{:key sub-attr-id}
             [sub-attr sub-name {}
              [object-inputs
               {:group               group
                :mapped-to           mapped-to
                :attr-name           name
                :on-mapped-to-change identity
                :on-group-change     identity}]])]])
       (for [{:keys [id name sub-items]} arrays]
         ^{:key id}
         [attribute name {}
          [array-attr id
           (for [{sub-item-id :id mapped-to :mapped-to type :type group :group} sub-items]
             ^{:key sub-item-id}
             [array-attr-item
              {:group               group
               :type                type
               :mapped-to           mapped-to
               :on-remove           #(rf/dispatch [:mapping/>remove-sub-item id sub-item-id])
               :on-type-change      #(rf/dispatch [:mapping/>set-array-type id sub-item-id (-> % .-target .-value)])
               :on-group-change     #(rf/dispatch [:mapping/>set-array-group id sub-item-id (-> % .-target .-value)])
               :on-mapped-to-change #(rf/dispatch [:mapping/>set-array-mapped-to id sub-item-id (-> % .-target .-value)])}])]])))])

