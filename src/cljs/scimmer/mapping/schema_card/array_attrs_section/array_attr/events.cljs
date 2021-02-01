(ns scimmer.mapping.schema-card.array-attrs-section.array-attr.events
  (:require
    [re-frame.core :as rf]
    [ajax.core :as ajax]
    [reitit.frontend.easy :as rfe]
    [reitit.frontend.controllers :as rfc]
    [scimmer.app-db :as app-db]
    [scimmer.services.mapping :refer [build-resource]]
    [scimmer.mapping.schema-card.events :refer [attr-interceptor get-attr-idx]]))

(defn array-attr-remove [[_name _props schema]]
  (not= (:type schema) :vector))

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
  :mapping/>add-sub-item
  [(attr-interceptor array-attr-remove)]
  (fn [attrs [_ id]]
    (let [default-sub-attr [:type1
                            nil
                            {:type     :map,
                             :children [[:type nil {:type :=, :children [:type1]}]
                                        [:value
                                         {:scimmer.services.schema/mapping :user/value1}
                                         {:type string?}]]}]
          idx (->> attrs
                   (map-indexed (fn [idx itm] [idx itm]))
                   (some (fn [[idx item]]
                           (and (= id (-> item meta :id)) idx))))]
      (update-in attrs [idx 2 :children 0 :children] conj default-sub-attr))))

(rf/reg-event-fx
  :mapping/>remove-sub-item
  (fn [{db :db} [_ id name]]
    (let [attrs (get-in db [:mapping :children])
          updated-attr
          (-> (some #(and (= (-> % meta :id) id) %) attrs)
              vec
              (update-in [2 :children 0 :children]
                         (fn [sub-items] (remove #(= (first %) name) sub-items))))
          new-attrs (map (fn [attr] (if (= (-> attr meta :id) id) updated-attr attr)) attrs)]
      {:db (assoc-in db [:mapping :children] new-attrs)})))
