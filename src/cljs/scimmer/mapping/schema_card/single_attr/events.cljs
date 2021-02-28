(ns scimmer.mapping.schema-card.single-attr.events
  (:require
    [re-frame.core :as rf]
    [ajax.core :as ajax]
    [reitit.frontend.easy :as rfe]
    [reitit.frontend.controllers :as rfc]
    [scimmer.app-db :as app-db]
    [scimmer.services.mapping :refer [build-resource]]))

;; update
(rf/reg-event-db
  :mapping/>set-single-collection
  [(rf/path :schema)]
  (fn [schema [_ id collection]]
    (assoc-in schema [id :collection] collection)))

(rf/reg-event-db
  :mapping/>set-ext-single-collection
  [(rf/path :extensions)]
  (fn [exts [_ ext-id id collection]]
    (assoc-in exts [ext-id :attrs id :collection] collection)))

(rf/reg-event-db
  :mapping/>set-single-mapped-to
  [(rf/path :schema)]
  (fn [schema [_ id mapped-to]]
    (assoc-in schema [id :mapped-to] mapped-to)))

(rf/reg-event-db
  :mapping/>set-ext-single-mapped-to
  [(rf/path :extensions)]
  (fn [exts [_ ext-id id mapped-to]]
    (assoc-in exts [ext-id :attrs id :mapped-to] mapped-to)))

;; add
(def default-single
  {:type      :single
   :name      "newAttribute"
   :mapped-to "new_attribute"
   :collection     "user"})

(rf/reg-event-db
  :mapping/>add-single-attr
  [(rf/path :schema)]
  (fn [schema _]
    (assoc schema (random-uuid) default-single)))

(rf/reg-event-db
  :mapping/>add-ext-single-attr
  [(rf/path :extensions)]
  (fn [exts [_ ext-id]]
    (assoc-in exts [ext-id :attrs (random-uuid)] default-single)))

