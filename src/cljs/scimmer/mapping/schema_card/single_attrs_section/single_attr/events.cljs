(ns scimmer.mapping.schema-card.single-attrs-section.single-attr.events
  (:require
    [re-frame.core :as rf]
    [ajax.core :as ajax]
    [reitit.frontend.easy :as rfe]
    [reitit.frontend.controllers :as rfc]
    [scimmer.app-db :as app-db]
    [scimmer.services.mapping :refer [build-resource]]
    [scimmer.mapping.schema-card.events :refer [attr-interceptor get-attr-idx]]))

;; update
(rf/reg-event-db
  :mapping/>set-single-group
  [(rf/path :schema)]
  (fn [schema [_ id group]]
    (assoc-in schema [id :group] group)))

(rf/reg-event-db
  :mapping/>set-single-mapped-to
  [(rf/path :schema)]
  (fn [schema [_ id mapped-to]]
    (assoc-in schema [id :mapped-to] mapped-to)))

;; add
(rf/reg-event-db
  :mapping/>add-single-attr
  [(rf/path :schema)]
  (fn [schema _]
    (let [default {:type      :single
                   :name      "newAttribute"
                   :mapped-to "new_attribute"
                   :group     "user"}]
      (assoc schema (random-uuid) default))))

