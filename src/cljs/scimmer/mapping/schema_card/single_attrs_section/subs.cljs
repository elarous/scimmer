(ns scimmer.mapping.schema-card.single-attrs-section.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
  :mapping/single-attrs
  (fn [db _]
    (->> (get-in db [:schema :attrs])
         (map (fn [[k v]] (assoc v :id k)))
         (filter #(= :single (:type %))))))


(comment
  @(rf/subscribe [:mapping/single-attrs])


  ,)
