(ns scimmer.mapping.schema-card.object-attrs-section.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
  :mapping/object-attrs
  (fn [db _]
    (let [assoc-id (fn [[k v]] (assoc v :id k))]
      (->> (get-in db [:schema :attrs])
           (map assoc-id)
           (filter #(= "object" (:type %)))
           (map #(update % :sub-attrs (partial map assoc-id)))))))

(comment
  @(rf/subscribe [:mapping/object-attrs])


  ,)
