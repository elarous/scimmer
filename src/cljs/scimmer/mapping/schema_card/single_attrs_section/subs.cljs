(ns scimmer.mapping.schema-card.single-attrs-section.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
  :mapping/single-attrs
  (fn [db _]
    (->> (get db :schema)
         (map (fn [[k v]] (assoc v :id k)))
         (filter #(= :single (:type %))))))

