(ns scimmer.mapping.events
  (:require
    [re-frame.core :as rf]
    [ajax.core :as ajax]
    [reitit.frontend.easy :as rfe]
    [reitit.frontend.controllers :as rfc]
    [scimmer.app-db :as app-db]))

(def single-attr-interceptor
  (rf/->interceptor
    :id :single-attr-interceptor
    :before (fn [context]
              (let [original-db (rf/get-coeffect context :db)
                    single-attrs
                    (-> original-db
                        (get-in [:mapping :children])
                        (as-> all-attrs
                              (filterv (fn [[_name _props schema]]
                                         (not (contains? #{:vector :map} (:type schema)))) all-attrs)))]
                (-> context
                    (assoc :temp-db original-db)
                    (rf/assoc-coeffect :db single-attrs))))
    :after (fn [context]
             (let [original-db (:temp-db context)
                   db-without-old-attrs (update-in original-db [:mapping :children]
                                                   #(filterv (fn [[_name _props schema]]
                                                               (contains? #{:vector :map} (:type schema))) %))
                   new-attrs-vec (rf/get-effect context :db ::not-found)
                   new-db (update-in db-without-old-attrs [:mapping :children] concat new-attrs-vec)]
               (rf/assoc-effect context :db new-db)))))

(rf/reg-event-db
  :mapping/>set-single-attr
  [rf/debug single-attr-interceptor]
  (fn [single-attrs [_ attr]]
    (conj single-attrs [:hope {:scimmer.services.schema/mapping :go/here} {:type int?}])))






