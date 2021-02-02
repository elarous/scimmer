(ns scimmer.mapping.schema-card.object-attrs-section.views
  (:require ["react" :as re]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [herb.core :refer [<class]]
            ["grommet" :refer [Anchor Grommet Button Heading Select Grid Box TextInput FormField Layer]]
            ["grommet-icons" :refer [Add]]
            [scimmer.utils :refer [debounce]]
            [scimmer.theme :refer [colors]]
            [scimmer.mapping.schema-card.object-attrs-section.subs]
            [scimmer.mapping.events]
            [scimmer.mapping.card.views :refer [card header]]
            [scimmer.mapping.schema-card.attribute.views :refer [attribute]]
            [scimmer.mapping.schema-card.object-attrs-section.object-attr.views :refer [object-attr sub-attr object-inputs]]
            [scimmer.mapping.schema-card.utils :refer [get-entity-mapping get-mapping-attr-item]]))

(defn objects-section [{:keys [set-attr remove-attr]}]
  (let [object-attrs @(rf/subscribe [:mapping/object-attrs])
        set-sub-attr (fn [attr-id sub-attr-id sub-attr]
                       (rf/dispatch [:mapping/>set-sub-attr attr-id sub-attr-id sub-attr]))
        remove-sub-attr (fn [attr-id sub-attr-id]
                          (rf/dispatch [:mapping/>remove-sub-attr attr-id sub-attr-id]))]
    [:<>
     (for [{:keys [id name sub-attrs]} object-attrs]
       ^{:key id}
       [attribute name {:on-change #(set-attr id (-> % .-target .-value))
                        :on-remove #(remove-attr id)}
        [object-attr
         {:on-add-sub-attr #(rf/dispatch [:mapping/>add-sub-attr id])}
         (for [{sub-attr-id :id sub-name :name mapped-to :mapped-to group :group} sub-attrs]
           ^{:key sub-attr-id}
           [sub-attr sub-name {:on-change #(set-sub-attr id sub-attr-id (-> % .-target .-value))
                               :on-remove #(remove-sub-attr id sub-attr-id)}

            [object-inputs
             {:group               group
              :mapped-to           mapped-to
              :attr-name           name
              :on-mapped-to-change #(rf/dispatch [:mapping/>set-object-mapped-to id sub-attr-id (-> % .-target .-value)])
              :on-group-change     #(rf/dispatch [:mapping/>set-object-group id sub-attr-id (-> % .-target .-value)])}]])]])]))


