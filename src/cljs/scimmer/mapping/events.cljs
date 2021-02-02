(ns scimmer.mapping.events
  (:require
    [re-frame.core :as rf]
    [ajax.core :as ajax]
    [reitit.frontend.easy :as rfe]
    [reitit.frontend.controllers :as rfc]
    [scimmer.app-db :as app-db]
    [scimmer.services.mapping :refer [build-resource]]))

(rf/reg-event-db
  :mapping/>resource->entities
  (fn [db [_ _]]
    (let [schema (:mapping db)
          resource (:resource db)]
      (assoc db :entities (build-resource schema resource [] {} {})))))

;; TODO: gen keys for array items too and probably i split this function in to type specific functions (for objects and arrays)
(rf/reg-event-db
  :mapping/>gen-keys
  [(rf/path :mapping :children)]
  (fn [mapping _]
    (map (fn [item]
           (def item item)
           (if (= (-> item (nth 2) :type) :map)
             ;; If the attribute is an object, add keys to the sub-attrs also
             (-> item
                 (update-in [2 :children]
                            (fn [sa] (map #(with-meta % {:id (random-uuid)}) sa)))
                 (with-meta {:id (random-uuid)}))
             (with-meta item {:id (random-uuid)})))
         mapping)))
