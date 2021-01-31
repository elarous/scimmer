(ns scimmer.mapping.schema-card.input.views
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [herb.core :refer [<class join]]
            ["grommet" :refer [Anchor Grommet Button Heading Select Grid Box TextInput FormField]]
            ["grommet-icons" :refer [AddCircle Trash]]
            [scimmer.mapping.schema-card.input.styles :as stl]))

(defn input [{:keys [name label value on-change]}]
  (r/with-let [timeout-duration 500
               timeout-fn #(rf/dispatch [:mapping/>resource->entities])
               timeout (r/atom (js/setTimeout timeout-fn timeout-duration))]
    [:> FormField {:name  name
                   :class (<class stl/attr-form-field)}
     [:> TextInput {:placeholder   label
                    :size          "small"
                    :class         (<class stl/attr-input)
                    :name          name
                    :default-value value
                    :on-change     (fn [e]
                                     (js/clearTimeout)
                                     (reset! timeout (js/setTimeout timeout-fn timeout-duration))
                                     (on-change e))}]]))