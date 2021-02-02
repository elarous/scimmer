(ns scimmer.mapping.schema-card.object-attrs-section.object-attr.events
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
  :mapping/>add-sub-attr
  [(rf/path :schema)]
  (fn [schema [_ attr-id]]
    (let [default-sub-attr {:name      "subAttr"
                            :mapped-to "sub_attr"
                            :group     "user"}]
      (assoc-in schema [attr-id :sub-attrs (random-uuid)] default-sub-attr))))

(rf/reg-event-db
  :mapping/>remove-sub-attr
  [(rf/path :schema)]
  (fn [schema [_ attr-id sub-attr-id]]
    (update-in schema [attr-id :sub-attrs] dissoc sub-attr-id)))

(rf/reg-event-db
  :mapping/>set-object-mapped-to
  [(rf/path :schema)]
  (fn [schema [_ attr-id sub-attr-id mapped-to]]
    (assoc-in schema [attr-id :sub-attrs sub-attr-id :mapped-to] mapped-to)))

(rf/reg-event-db
  :mapping/>set-object-group
  [(rf/path :schema)]
  (fn [schema [_ attr-id sub-attr-id group]]
    (assoc-in schema [attr-id :sub-attrs sub-attr-id :group] group)))

(rf/reg-event-db
  :mapping/>add-object-attr
  [(rf/path :schema)]
  (fn [schema _]
    (let [default {:type :object
                   :name "newObjectAttr"
                   :sub-attrs {}}]
      (assoc schema (random-uuid) default))))