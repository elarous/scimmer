(ns scimmer.mapping.schema-card.object-attr.events
  (:require
    [re-frame.core :as rf]
    [ajax.core :as ajax]
    [reitit.frontend.easy :as rfe]
    [reitit.frontend.controllers :as rfc]
    [scimmer.app-db :as app-db]
    [scimmer.services.mapping :refer [build-resource]]
    [scimmer.mapping.schema-card.events :refer [attr-interceptor get-attr-idx]]))

(rf/reg-event-db
  :mapping/>set-sub-attr
  [(rf/path :schema)]
  (fn [schema [_ attr-id sub-attr-id sub-attr]]
    (assoc-in schema [attr-id :sub-attrs sub-attr-id :name] sub-attr)))

(rf/reg-event-db
  :mapping/>set-ext-sub-attr
  [(rf/path :extensions)]
  (fn [exts [_ ext-id attr-id sub-attr-id sub-attr]]
    (assoc-in exts [ext-id :attrs attr-id :sub-attrs sub-attr-id :name] sub-attr)))

(def default-sub-attr
  {:name      "subAttr"
   :mapped-to "sub_attr"
   :group     "user"})

(rf/reg-event-db
  :mapping/>add-sub-attr
  [(rf/path :schema)]
  (fn [schema [_ attr-id]]
    (assoc-in schema [attr-id :sub-attrs (random-uuid)] default-sub-attr)))

(rf/reg-event-db
  :mapping/>add-ext-sub-attr
  [(rf/path :extensions)]
  (fn [exts [_ ext-id attr-id]]
    (assoc-in exts [ext-id :attrs attr-id :sub-attrs (random-uuid)] default-sub-attr)))

(rf/reg-event-db
  :mapping/>remove-sub-attr
  [(rf/path :schema)]
  (fn [schema [_ attr-id sub-attr-id]]
    (update-in schema [attr-id :sub-attrs] dissoc sub-attr-id)))

(rf/reg-event-db
  :mapping/>remove-ext-sub-attr
  [(rf/path :extensions)]
  (fn [exts [_ ext-id attr-id sub-attr-id]]
    (update-in exts [ext-id :attrs attr-id :sub-attrs] dissoc sub-attr-id)))

(rf/reg-event-db
  :mapping/>set-object-mapped-to
  [(rf/path :schema)]
  (fn [schema [_ attr-id sub-attr-id mapped-to]]
    (assoc-in schema [attr-id :sub-attrs sub-attr-id :mapped-to] mapped-to)))

(rf/reg-event-db
  :mapping/>set-ext-object-mapped-to
  [(rf/path :extensions)]
  (fn [exts [_ ext-id attr-id sub-attr-id mapped-to]]
    (assoc-in exts [ext-id :attrs attr-id :sub-attrs sub-attr-id :mapped-to] mapped-to)))

(rf/reg-event-db
  :mapping/>set-object-group
  [(rf/path :schema)]
  (fn [schema [_ attr-id sub-attr-id group]]
    (assoc-in schema [attr-id :sub-attrs sub-attr-id :group] group)))

(rf/reg-event-db
  :mapping/>set-ext-object-group
  [(rf/path :extensions)]
  (fn [exts [_ ext-id attr-id sub-attr-id group]]
    (assoc-in exts [ext-id :attrs attr-id :sub-attrs sub-attr-id :group] group)))

(def default-object-attr
  {:type      :object
   :name      "newObjectAttr"
   :sub-attrs {}})

(rf/reg-event-db
  :mapping/>add-object-attr
  [(rf/path :schema)]
  (fn [schema _]
    (assoc schema (random-uuid) default-object-attr)))

(rf/reg-event-db
  :mapping/>add-ext-object-attr
  [(rf/path :extensions)]
  (fn [exts [_ ext-id]]
    (assoc-in exts [ext-id :attrs (random-uuid)] default-object-attr)))

