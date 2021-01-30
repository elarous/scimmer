(ns scimmer.mapping.events
  (:require
    [re-frame.core :as rf]
    [ajax.core :as ajax]
    [reitit.frontend.easy :as rfe]
    [reitit.frontend.controllers :as rfc]
    [scimmer.app-db :as app-db]
    [scimmer.services.mapping :refer [build-resource]]))

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

;; helper functions
(defn get-attr-idx [attrs name]
  (->> attrs
       (map-indexed (fn [idx itm] [idx itm]))
       (some (fn [[idx [attr-name _props _schema]]] (and (= name attr-name) idx)))))
;;



(rf/reg-event-db
  :mapping/>update-single-attr
  [(attr-interceptor single-attr-remove)]
  (fn [single-attrs [_ {:keys [name entity mapping]}]]
    (let [target-idx (get-attr-idx single-attrs name)]
      (assoc-in single-attrs [target-idx 1 :scimmer.services.schema/mapping]
                (keyword mapping entity)))))

(rf/reg-event-db
  :mapping/>update-map-attr
  [(attr-interceptor map-attr-remove)]
  (fn [map-attrs [_ {:keys [name subattr entity mapping]}]]
    (let [target-idx (get-attr-idx map-attrs name)
          children (-> map-attrs (nth target-idx) (nth 2) :children)
          target-subattr-idx (get-attr-idx children subattr)]
      (assoc-in map-attrs [target-idx 2 :children target-subattr-idx 1 :scimmer.services.schema/mapping]
                (keyword mapping entity)))))

(rf/reg-event-db
  :mapping/>update-array-attr
  [(attr-interceptor array-attr-remove)]
  (fn [array-attrs [_ {:keys [name idx mapping entity type]}]]
    (let [target-idx (get-attr-idx array-attrs name)]
      (-> array-attrs
          (assoc-in [target-idx 2 :children 0 :children idx 0] type)
          (assoc-in [target-idx 2 :children 0 :children idx 2 :children 0 2 :children 0] type)
          (assoc-in
            [target-idx 2 :children 0 :children idx 2 :children 1 1 :scimmer.services.schema/mapping]
            (keyword mapping entity))))))

;;
(rf/reg-event-db
  :mapping/>resource->entities
  (fn [db [_ _]]
    (let [schema (:mapping db)
          resource (:resource db)]
      (assoc db :entities (build-resource schema resource [] {} {})))))
;;

(rf/reg-event-fx
  :mapping/>set-resource
  (fn [{db :db} [_ new-value]]
    {:db       (assoc db :resource (js->clj (.parse js/JSON new-value) :keywordize-keys true))
     :dispatch [:mapping/>resource->entities]}))

;;
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
  :mapping/>set-sub-attr
  (fn [{db :db} [_ attr-id sub-attr-id sub-attr]]
    (let [attrs (get-in db [:mapping :children])
          idx (->> attrs
                   (map-indexed (fn [idx itm] [idx itm]))
                   (some (fn [[idx item]]
                           (and (= attr-id (-> item meta :id)) idx))))
          sub-attrs (get-in (vec attrs) [idx 2 :children])
          sub-attr-idx (->> sub-attrs
                            (map-indexed (fn [idx itm] [idx itm]))
                            (some (fn [[idx item]]
                                    (and (= sub-attr-id (-> item meta :id)) idx))))
          new-sub-attr (vec (concat [(keyword sub-attr)] (rest (nth sub-attrs sub-attr-idx))))
          new-sub-attrs (map-indexed (fn [i item]
                                       (if (= i sub-attr-idx)
                                         (with-meta new-sub-attr (meta item))
                                         item)) sub-attrs)]
      {:db       (-> db
                     (update-in [:mapping :children] vec)
                     (assoc-in [:mapping :children idx 2 :children] (vec new-sub-attrs)))
       :dispatch [:mapping/>resource->entities]})))

(rf/reg-event-db
  :mapping/>add-single-attr
  [(attr-interceptor single-attr-remove)]
  (fn [attrs _]
    (let [default-attr
          (with-meta
            [:newAttribute {:scimmer.services.schema/mapping :user/new_attribute} {:type string?}]
            {:id (random-uuid)})]
      (conj attrs default-attr))))

(rf/reg-event-db
  :mapping/>add-map-attr
  [(attr-interceptor map-attr-remove)]
  (fn [attrs _]
    (let [default-attr
          (with-meta
            [:newAttribute nil
             {:type     :map
              :children [(with-meta
                           [:subAttribute {:scimmer.services.schema/mapping :user/sub_attribute} {:type string?}]
                           {:id (random-uuid)})]}]
            {:id (random-uuid)})]
      (conj attrs default-attr))))

(rf/reg-event-db
  :mapping/>add-array-attr
  [(attr-interceptor array-attr-remove)]
  (fn [attrs _]
    (let [default-attr
          (with-meta
            [:newAttribute
             nil
             {:type     :vector,
              :children [{:type       :multi,
                          :properties {:dispatch :type},
                          :children   [[:type1
                                        nil
                                        {:type     :map,
                                         :children [[:type nil {:type :=, :children [:type1]}]
                                                    [:value
                                                     {:scimmer.services.schema/mapping :user/value1}
                                                     {:type string?}]]}]]}]}]

            {:id (random-uuid)})]
      (conj attrs default-attr))))

(rf/reg-event-db
  :mapping/>gen-keys
  [(rf/path :mapping :children)]
  (fn [mapping _]
    (map (fn [item]
           (def item item)
           (if (= (-> item (nth 2) :type) :map)
             ;; If the attribute is an object, add keys to the sub-attrs also
             (-> item
                 (update-in [2 :children]
                            (fn [sa] (map #(with-meta % {:id (random-uuid)}) sa)))
                 (with-meta {:id (random-uuid)}))
             (with-meta item {:id (random-uuid)})))
         mapping)))

