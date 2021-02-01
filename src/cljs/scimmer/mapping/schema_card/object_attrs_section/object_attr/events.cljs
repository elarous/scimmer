(ns scimmer.mapping.schema-card.object-attrs-section.object-attr.events
  (:require
    [re-frame.core :as rf]
    [ajax.core :as ajax]
    [reitit.frontend.easy :as rfe]
    [reitit.frontend.controllers :as rfc]
    [scimmer.app-db :as app-db]
    [scimmer.services.mapping :refer [build-resource]]
    [scimmer.mapping.schema-card.events :refer [attr-interceptor get-attr-idx]]))

(defn map-attr-remove [[_name _props schema]]
  (not= (:type schema) :map))

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
  :mapping/>add-sub-attr
  [(attr-interceptor map-attr-remove)]
  (fn [attrs [_ id]]
    (let [default-sub-attr (with-meta [:newSubAttr {:scimmer.services.schema/mapping :user/newSubAttr} {:type string?}]
                                      {:id (random-uuid)})
          idx (->> attrs
                   (map-indexed (fn [idx itm] [idx itm]))
                   (some (fn [[idx item]]
                           (and (= id (-> item meta :id)) idx))))]
      (update-in attrs [idx 2 :children] conj default-sub-attr))))

(rf/reg-event-fx
  :mapping/>remove-sub-attr
  (fn [{db :db} [_ attr-id sub-attr-id]]
    (let [attrs (vec (get-in db [:mapping :children]))
          idx (->> attrs
                   (map-indexed (fn [idx itm] [idx itm]))
                   (some (fn [[idx item]]
                           (and (= attr-id (-> item meta :id)) idx))))]
      {:db       (-> db
                     (update-in [:mapping :children] vec)
                     (update-in [:mapping :children idx 2 :children]
                                #(remove (fn [sub-item] (= sub-attr-id (-> sub-item meta :id))) %)))
       :dispatch [:mapping/>resource->entities]})))

