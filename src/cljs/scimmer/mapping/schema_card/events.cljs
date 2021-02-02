(ns scimmer.mapping.schema-card.events
  (:require
    [re-frame.core :as rf]
    [ajax.core :as ajax]
    [reitit.frontend.easy :as rfe]
    [reitit.frontend.controllers :as rfc]
    [scimmer.app-db :as app-db]
    [scimmer.services.mapping :refer [build-resource]]))

;; Interceptors
(defn attr-interceptor [filtering-fn]
  (rf/->interceptor
    :id :attr-interceptor
    :before (fn [context]
              (let [original-db (rf/get-coeffect context :db)
                    single-attrs
                    (-> original-db
                        (get-in [:mapping :children])
                        (as-> all-attrs
                              (filterv (complement filtering-fn) all-attrs)))]
                (-> context
                    (assoc :temp-db original-db)
                    (rf/assoc-coeffect :db single-attrs))))
    :after (fn [context]
             (let [original-db (:temp-db context)
                   db-without-old-attrs (update-in original-db [:mapping :children]
                                                   #(filterv filtering-fn %))
                   new-attrs-vec (rf/get-effect context :db ::not-found)
                   new-db (update-in db-without-old-attrs [:mapping :children] concat new-attrs-vec)]
               (rf/assoc-effect context :db new-db)))))

;; Helper functions
(defn get-attr-idx [attrs name]
  (->> attrs
       (map-indexed (fn [idx itm] [idx itm]))
       (some (fn [[idx [attr-name _props _schema]]] (and (= name attr-name) idx)))))

;; Events
(rf/reg-event-db
  :mapping/>set-attr
  [(rf/path :schema)]
  (fn [schema [_ id name]]
    (assoc-in schema [id :name] name)))

(rf/reg-event-db
  :mapping/>remove-attr
  [(rf/path :schema)]
  (fn [schema [_ id]]
    (dissoc schema id)))

