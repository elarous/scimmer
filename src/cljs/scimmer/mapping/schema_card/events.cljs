(ns scimmer.mapping.schema-card.events
  (:require
    [re-frame.core :as rf]
    [ajax.core :as ajax]
    [reitit.frontend.easy :as rfe]
    [reitit.frontend.controllers :as rfc]
    [scimmer.app-db :as app-db]
    [scimmer.services.mapping :refer [build-resource]]))

; Events
(rf/reg-event-db
  :mapping/>set-attr
  [(rf/path :schema)]
  (fn [schema [_ id name]]
    (assoc-in schema [:attrs id :name] name)))

(rf/reg-event-db
  :mapping/>remove-attr
  [(rf/path :schema)]
  (fn [schema [_ id]]
    (update schema :attrs dissoc id)))

(rf/reg-event-fx
  :mapping/>save
  (fn [{db :db} _]
    (js/console.log "Saving schema ...")
    {:db db}))


