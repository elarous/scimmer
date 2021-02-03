(ns scimmer.mapping.subs
  (:require
    [re-frame.core :as rf]
    [ajax.core :as ajax]
    [reitit.frontend.easy :as rfe]
    [reitit.frontend.controllers :as rfc]
    [clojure.edn :as edn]
    [clojure.data :as data]
    [scimmer.app-db :as app-db]
    [scimmer.services.mapping :refer [build-resource]]
    [scimmer.mapping.utils :refer [single-attr->schema object-attr->schema array-attr->schema]]))

;; Resource subscriptions
(rf/reg-sub
  :mapping/resource
  (fn [db _]
    (:resource db)))

(rf/reg-sub
  :mapping/resource-json
  :<- [:mapping/resource]
  (fn [resource]
    (clj->js resource)))

(rf/reg-sub
  :mapping/resource-json-str
  :<- [:mapping/resource-json]
  (fn [json]
    (.stringify js/JSON json nil 2)))


;; Entities subscriptions
(rf/reg-sub
  :mapping/entities
  :<- [:mapping/resource]
  :<- [:mapping/single-attrs]
  :<- [:mapping/object-attrs]
  :<- [:mapping/array-attrs]
  (fn [[resource singles objects arrays] _]
    (js/console.log [resource singles objects arrays])
    (let [single-attrs (map single-attr->schema singles)
          object-attrs (map object-attr->schema objects)
          array-attrs (map array-attr->schema arrays)
          schema (hash-map :type :map
                           :children (concat single-attrs object-attrs array-attrs))]
      (build-resource schema resource [] {} {}))))

(rf/reg-sub
  :mapping/entities-json
  :<- [:mapping/entities]
  (fn [entities _]
    (.stringify js/JSON (clj->js entities) nil 2)))



