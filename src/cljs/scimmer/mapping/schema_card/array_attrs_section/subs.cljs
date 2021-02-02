(ns scimmer.mapping.schema-card.array-attrs-section.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
  :mapping/array-attrs
  (fn [db _]
    (let [assoc-id (fn [[k v]] (assoc v :id k))]
      (->> (get db :schema)
           (map assoc-id)
           (filter #(= :array (:type %)))
           (map #(update % :sub-items (partial map assoc-id)))))))

