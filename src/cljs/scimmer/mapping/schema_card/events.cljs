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
(rf/reg-event-fx
  :mapping/>set-attr
  (fn [{db :db} [_ id name]]
    (let [attrs (get-in db [:mapping :children])
          idx (->> attrs
                   (map-indexed (fn [idx itm] [idx itm]))
                   (some (fn [[idx item]]
                           (and (= id (-> item meta :id)) idx))))
          new-target (vec (concat [(keyword name)] (rest (nth attrs idx))))
          new-attrs (map-indexed (fn [i item] (if (= i idx)
                                                (with-meta new-target (meta item))
                                                item)) attrs)]
      {:db       (assoc-in db [:mapping :children] (vec new-attrs))
       :dispatch [:mapping/>resource->entities]})))

(rf/reg-event-fx
  :mapping/>remove-attr
  (fn [{db :db} [_ id]]
    {:db       (update-in db [:mapping :children] (fn [attrs] (remove #(= (-> % meta :id) id) attrs)))
     :dispatch [:mapping/>resource->entities]}))
