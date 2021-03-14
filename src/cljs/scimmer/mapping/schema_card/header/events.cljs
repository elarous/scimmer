(ns scimmer.mapping.schema-card.header.events
  (:require [re-frame.core :as rf :refer [path]]))

(defn new-schema []
  {:id         (random-uuid)
   :resource   "user"
   :name       "newSchema"
   :is-default false
   :attrs      {}
   :extensions {}})

(rf/reg-event-db
 :mapping/>set-schema-name
 [(path :schema)]
 (fn [schema [_ new-name]]
   (assoc schema :name new-name)))

(rf/reg-event-db
 :mapping/>new-schema
 (fn [db _]
   (let [schema (new-schema)]
     (-> db
         (assoc :schema-saved? false)
         (assoc :schema schema)
         (update :schemas conj schema)))))

