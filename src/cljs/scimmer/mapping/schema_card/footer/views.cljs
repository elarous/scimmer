(ns scimmer.mapping.schema-card.footer.views
  (:require [re-frame.core :as rf]
            [herb.core :refer [<class join]]
            ["grommet" :refer [Button]]
            [scimmer.mapping.schema-card.footer.styles :as stl]))

(defn footer [{:keys [on-save]}]
  [:div {:class (<class stl/footer)}
   [:> Button {:className (join (<class stl/button) (<class stl/save-btn))
               :on-click on-save
               :label     "Save"}]])

