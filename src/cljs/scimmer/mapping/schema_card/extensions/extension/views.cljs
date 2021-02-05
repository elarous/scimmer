(ns scimmer.mapping.schema-card.extensions.extension.views
  (:require [re-frame.core :as rf]
            [reagent.core :as r]
            [herb.core :refer [<class]]
            ["grommet" :refer [Anchor]]
            ["grommet-icons" :refer [Trash Down]]
            [scimmer.theme :refer [colors]]
            [scimmer.mapping.schema-card.extensions.extension.events]
            [scimmer.mapping.schema-card.top-actions.views :refer [top-actions-ext]]
            [scimmer.mapping.schema-card.extensions.extension.styles :as stl]
            [scimmer.mapping.schema-card.attribute.views :refer [attribute]]
            [scimmer.mapping.schema-card.single-attr.views :refer [single-attr]]
            [scimmer.mapping.schema-card.object-attr.views :refer [object-attr sub-attr object-inputs]]
            [scimmer.mapping.schema-card.array-attr.views :refer [array-attr array-attr-item]]))

(defn- singles-section [{:keys [set-attr remove-attr ext]} singles]
  [:div
   (for [{:keys [id name group mapped-to]} singles]
     [attribute name {:on-change #(set-attr id (-> % .-target .-value))
                      :on-remove #(remove-attr id)}
      [single-attr {:group               group
                    :mapped-to           mapped-to
                    :on-group-change     #(rf/dispatch [:mapping/>set-ext-single-group (:id ext) id %])
                    :on-mapped-to-change #(rf/dispatch [:mapping/>set-ext-single-mapped-to (:id ext) id %])}]])])

(defn- objects-section [{:keys [set-attr remove-attr ext]} objects]
  [:div
   (for [{:keys [id name sub-attrs]} objects]
     ^{:key id}
     [attribute name {:on-change #(set-attr id (-> % .-target .-value))
                      :on-remove #(remove-attr id)}
      [object-attr
       {:on-add-sub-attr #(rf/dispatch [:mapping/>add-ext-sub-attr (:id ext) id])}
       (for [{sub-attr-id :id sub-name :name mapped-to :mapped-to group :group} sub-attrs]
         ^{:key sub-attr-id}
         [sub-attr sub-name
          {:on-change #(rf/dispatch [:mapping/>set-ext-sub-attr (:id ext) id sub-attr-id (-> % .-target .-value)])
           :on-remove #(rf/dispatch [:mapping/>remove-ext-sub-attr (:id ext) id sub-attr-id])}
          [object-inputs
           {:group               group
            :mapped-to           mapped-to
            :attr-name           name
            :on-mapped-to-change #(rf/dispatch [:mapping/>set-ext-object-mapped-to (:id ext) id sub-attr-id (-> % .-target .-value)])
            :on-group-change     #(rf/dispatch [:mapping/>set-ext-object-group (:id ext) id sub-attr-id (-> % .-target .-value)])}]])]])])

(defn- arrays-section [{:keys [set-attr remove-attr ext]} arrays]
  [:div
   (for [{:keys [id name sub-items]} arrays]
     ^{:key id}
     [attribute name {:on-change #(set-attr id (-> % .-target .-value))
                      :on-remove #(remove-attr id)}
      [array-attr
       {:on-add #(rf/dispatch [:mapping/>add-ext-sub-item (:id ext) id])}
       (for [{sub-item-id :id mapped-to :mapped-to type :type group :group} sub-items]
         ^{:key sub-item-id}
         [array-attr-item
          {:group               group
           :type                type
           :mapped-to           mapped-to
           :on-remove           #(rf/dispatch [:mapping/>remove-ext-sub-item (:id ext) id sub-item-id])
           :on-type-change      #(rf/dispatch [:mapping/>set-ext-array-type (:id ext) id sub-item-id (-> % .-target .-value)])
           :on-group-change     #(rf/dispatch [:mapping/>set-ext-array-group (:id ext) id sub-item-id (-> % .-target .-value)])
           :on-mapped-to-change #(rf/dispatch [:mapping/>set-ext-array-mapped-to (:id ext) id sub-item-id (-> % .-target .-value)])}])]])])


(defn extension [ext]
  (r/with-let [collapsed? (r/atom false)]
    [:div
     [:div {:class (<class stl/header)}
      [:> Anchor {:on-click #(swap! collapsed? not)
                  :margin   "xsmall"
                  :size     "medium"
                  :color    (:secondary colors)
                  :icon     (r/create-element Down #js {:size      "small"
                                                        :className (<class stl/down-icon @collapsed?)})}]
      [:input {:class        (<class stl/label)
               :defaultValue (:label ext)
               :on-change    #(rf/dispatch [:mapping/>set-ext-label (:id ext) (-> % .-target .-value)])
               :type         "text"}]
      [:> Anchor {:on-click #(rf/dispatch [:mapping/>remove-ext (:id ext)])
                  :margin   "xsmall"
                  :size     "medium"
                  :color    (:secondary colors)
                  :icon     (r/create-element Trash #js {:size      "small"
                                                         :className (<class stl/trash-icon)})}]]
     (when-not @collapsed?
       [:div
        [top-actions-ext (:id ext)]
        (let [set-attr #(rf/dispatch [:mapping/>set-ext-attr (:id ext) %1 %2])
              remove-attr #(rf/dispatch [:mapping/>remove-ext-attr (:id ext) %])
              singles @(rf/subscribe [:mapping/ext-singles (:id ext)])
              objects @(rf/subscribe [:mapping/ext-objects (:id ext)])
              arrays @(rf/subscribe [:mapping/ext-arrays (:id ext)])]
          [:div
           [singles-section {:set-attr set-attr :remove-attr remove-attr :ext ext} singles]
           [objects-section {:set-attr set-attr :remove-attr remove-attr :ext ext} objects]
           [arrays-section {:set-attr set-attr :remove-attr remove-attr :ext ext} arrays]])])]))

