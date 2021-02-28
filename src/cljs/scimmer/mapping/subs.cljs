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
    [scimmer.mapping.utils :refer [single-attr->schema object-attr->schema array-attr->schema extension->schema]]))

;; Schema subscriptions
(rf/reg-sub
 :mapping/schema
 (fn [db _]
   (:schema db)))

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
  :<- [:mapping/extensions]
  (fn [[resource singles objects arrays exts] _]
    (let [single-attrs (map single-attr->schema singles)
          object-attrs (map object-attr->schema objects)
          array-attrs (map array-attr->schema arrays)
          extensions (map extension->schema exts)
          schema (hash-map :type :map
                           :children (vec (concat single-attrs object-attrs array-attrs extensions)))]
      (try
        (build-resource schema resource [] {} {})
        (catch js/Error e (js/console.error e))))))

(rf/reg-sub
  :mapping/entities-json
  :<- [:mapping/entities]
  (fn [entities _]
    (.stringify js/JSON (clj->js entities) nil 2)))



