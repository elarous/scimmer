(ns scimmer.mapping.schema-card.extensions.subs
  (:require [re-frame.core :as rf]
            [scimmer.mapping.utils :refer [assoc-id]]))

(rf/reg-sub
  :mapping/extensions
  (fn [db _]
    (->> (get db :extensions)
         (map assoc-id))))

(rf/reg-sub
  :mapping/attrs
  (fn [db [_ id type]]
    (->> (get-in db [:extensions id])
         :attrs
         (map assoc-id)
         (filter #(= type (:type %))))))

(defn attr-getter
  "Helper higher order method, filter extension attrs by type"
  [type]
  (fn [[_ id]]
    (rf/subscribe [:mapping/attrs id type])))

(rf/reg-sub
  :mapping/ext-singles
  (attr-getter :single)
  (fn [singles] singles))

(rf/reg-sub
  :mapping/ext-objects
  (attr-getter :object)
  (fn [objects]
    (map #(update % :sub-attrs (partial map assoc-id)) objects)))

(rf/reg-sub
  :mapping/ext-arrays
  (attr-getter :array)
  (fn [arrays]
    (map #(update % :sub-items (partial map assoc-id)) arrays)))

