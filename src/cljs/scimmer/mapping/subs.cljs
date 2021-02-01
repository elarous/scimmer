(ns scimmer.mapping.subs
  (:require
    [re-frame.core :as rf]
    [ajax.core :as ajax]
    [reitit.frontend.easy :as rfe]
    [reitit.frontend.controllers :as rfc]
    [clojure.edn :as edn]
    [clojure.data :as data]
    [scimmer.app-db :as app-db]))

;; Resource subscriptions
(rf/reg-sub
  :mapping/resource
  (fn [db _]
    (:resource db)))

;; Entities subscriptions
(rf/reg-sub
  :mapping/entities
  (fn [db _]
    (:entities db)))
