(ns scimmer.mapping.subs
  (:require
    [re-frame.core :as rf]
    [ajax.core :as ajax]
    [reitit.frontend.easy :as rfe]
    [reitit.frontend.controllers :as rfc]
    [scimmer.app-db :as app-db]))

(rf/reg-sub
  :mapping/all-attrs
  (fn [db _]
    (get-in db [:mapping :children])))

(rf/reg-sub
  :mapping/single-attrs
  :<- [:mapping/all-attrs]
  (fn [all-attrs _]
    (filterv (fn [[_name _props schema]]
               (not (contains? #{:map :vector} (:type schema)))) all-attrs)))

