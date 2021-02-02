(ns scimmer.mapping.resource-card.events
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

(rf/reg-event-db
  :mapping/>set-resource
  (fn [db [_ str-resource]]
    (let [resource (js->clj (.parse js/JSON str-resource) :keywordize-keys true)]
      (assoc db :resource resource))))