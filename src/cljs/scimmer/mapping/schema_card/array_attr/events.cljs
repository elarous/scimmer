(ns scimmer.mapping.schema-card.array-attr.events
  (:require
    [re-frame.core :as rf]
    [ajax.core :as ajax]
    [reitit.frontend.easy :as rfe]
    [reitit.frontend.controllers :as rfc]
    [scimmer.app-db :as app-db]
    [scimmer.services.mapping :refer [build-resource]]
    [scimmer.mapping.schema-card.events :refer [attr-interceptor get-attr-idx]]))


(def default-sub-item
  {:type      "type1"
   :group     "user"
   :mapped-to "sub_item"})

(rf/reg-event-db
  :mapping/>add-sub-item
  [(rf/path :schema)]
  (fn [schema [_ attr-id]]
    (assoc-in schema [attr-id :sub-items (random-uuid)] default-sub-item)))

(rf/reg-event-db
  :mapping/>add-ext-sub-item
  [(rf/path :extensions)]
  (fn [exts [_ ext-id attr-id]]
    (assoc-in exts [ext-id :attrs attr-id :sub-items (random-uuid)] default-sub-item)))

(rf/reg-event-db
  :mapping/>remove-sub-item
  [(rf/path :schema)]
  (fn [schema [_ attr-id sub-item-id]]
    (update-in schema [attr-id :sub-items] dissoc sub-item-id)))

(rf/reg-event-db
  :mapping/>remove-ext-sub-item
  [(rf/path :extensions)]
  (fn [exts [_ ext-id attr-id sub-item-id]]
    (update-in exts [ext-id :attrs attr-id :sub-items] dissoc sub-item-id)))

(rf/reg-event-db
  :mapping/>set-array-type
  [(rf/path :schema)]
  (fn [schema [_ attr-id sub-item-id type]]
    (assoc-in schema [attr-id :sub-items sub-item-id :type] type)))

(rf/reg-event-db
  :mapping/>set-ext-array-type
  [(rf/path :extensions)]
  (fn [exts [_ ext-id attr-id sub-item-id type]]
    (assoc-in exts [ext-id :attrs attr-id :sub-items sub-item-id :type] type)))

(rf/reg-event-db
  :mapping/>set-array-mapped-to
  [(rf/path :schema)]
  (fn [schema [_ attr-id sub-item-id mapped-to]]
    (assoc-in schema [attr-id :sub-items sub-item-id :mapped-to] mapped-to)))

(rf/reg-event-db
  :mapping/>set-ext-array-mapped-to
  [(rf/path :extensions)]
  (fn [exts [_ ext-id attr-id sub-item-id mapped-to]]
    (assoc-in exts [ext-id :attrs attr-id :sub-items sub-item-id :mapped-to] mapped-to)))

(rf/reg-event-db
  :mapping/>set-array-group
  [(rf/path :schema)]
  (fn [schema [_ attr-id sub-item-id group]]
    (assoc-in schema [attr-id :sub-items sub-item-id :group] group)))

(rf/reg-event-db
  :mapping/>set-ext-array-group
  [(rf/path :extensions)]
  (fn [exts [_ ext-id attr-id sub-item-id group]]
    (assoc-in exts [ext-id :attrs attr-id :sub-items sub-item-id :group] group)))

(def default-array-attr
  {:type      :array
   :name      "newArrayAttr"
   :sub-items {}})

(rf/reg-event-db
  :mapping/>add-array-attr
  [(rf/path :schema)]
  (fn [schema _]
    (assoc schema (random-uuid) default-array-attr)))

(rf/reg-event-db
  :mapping/>add-ext-array-attr
  [(rf/path :extensions)]
  (fn [exts [_ ext-id]]
    (assoc-in exts [ext-id :attrs (random-uuid)] default-array-attr)))

