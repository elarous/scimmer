(ns scimmer.mapping.schema-card.attribute.views
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [herb.core :refer [<class join]]
            ["grommet" :refer [Anchor Grommet Button Heading Select Grid Box TextInput FormField]]
            ["grommet-icons" :refer [AddCircle Trash]]
            [scimmer.mapping.schema-card.attribute.styles :as stl]
            [scimmer.mapping.schema-card.attribute.svgs :refer [collapse-icon]]))


(defn attribute
  ([name body]
   (attribute name {} body))
  ([name {:keys [on-change on-remove]} body]
   (r/with-let [collapsed? (r/atom false)]
     [:div {:class (<class stl/container)}
      [:div {:class (<class stl/head-container)}
       [:div {:class (<class stl/head)}
        [:div {:on-click #(swap! collapsed? not)}
         [collapse-icon {:collapsed? @collapsed?}]]
        [:input {:class        (<class stl/head-input)
                 :defaultValue name
                 :on-change    on-change
                 :type         "text"}]]
       [:> Anchor {:icon     (r/create-element Trash #js {:className (<class stl/trash-icon)
                                                          :size      "xsmall"})
                   :on-click on-remove}]]

      (when-not @collapsed?
        [:div {:class (<class stl/body)} body])])))


