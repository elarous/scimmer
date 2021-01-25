(ns scimmer.mapping.events
  (:require
    [re-frame.core :as rf]
    [ajax.core :as ajax]
    [reitit.frontend.easy :as rfe]
    [reitit.frontend.controllers :as rfc]
    [scimmer.app-db :as app-db]))

;; functions to filter out other kinds of attrs
(defn single-attr-remove [[_name _props schema]]
  (contains? #{:vector :map} (:type schema)))

(defn map-attr-remove [[_name _props schema]]
  (not= (:type schema) :map))

(defn array-attr-remove [[_name _props schema]]
  (not= (:type schema) :vector))
;;

(defn attr-interceptor [filtering-fn]
  (rf/->interceptor
    :id :single-attr-interceptor
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

(rf/reg-event-db
  :mapping/>update-single-attr
  [(attr-interceptor single-attr-remove)]
  (fn [single-attrs [_ {:keys [name entity mapping]}]]
    (let [target-idx (->> single-attrs
                          (map-indexed (fn [idx itm] [idx itm]))
                          (some (fn [[idx [attr-name _props _schema]]] (and (= name attr-name) idx))))]
      (assoc-in single-attrs [target-idx 1 :scimmer.services.schema/mapping]
                (keyword mapping entity)))))

(rf/reg-event-db
  :mapping/>update-map-attr
  [(attr-interceptor map-attr-remove)]
  (fn [map-attrs [_ {:keys [name subattr entity mapping]}]]
    (let [target-idx (->> map-attrs
                          (map-indexed (fn [idx itm] [idx itm]))
                          (some (fn [[idx [attr-name _props _schema]]] (and (= name attr-name) idx))))
          children (-> map-attrs (nth target-idx) (nth 2) :children)
          target-subattr-idx (->> children
                                  (map-indexed (fn [idx itm] [idx itm]))
                                  (some (fn [[idx [attr-name _props _schema]]] (and (= subattr attr-name) idx))))]
      (assoc-in map-attrs [target-idx 2 :children target-subattr-idx 1 :scimmer.services.schema/mapping]
                (keyword mapping entity)))))

(rf/reg-event-db
  :mapping/>update-array-attr
  [(attr-interceptor array-attr-remove)]
  (fn [array-attrs [_ {:keys [name idx mapping entity type]}]]
    (let [target-idx (->> array-attrs
                          (map-indexed (fn [idx itm] [idx itm]))
                          (some (fn [[idx [attr-name _props _schema]]] (and (= name attr-name) idx))))]
      (-> array-attrs
          (assoc-in [target-idx 2 :children 0 :children idx 0] type)
          (assoc-in
            [target-idx 2 :children 0 :children idx 2 :children 1 1 :scimmer.services.schema/mapping]
            (keyword mapping entity))))))

