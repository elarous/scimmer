(ns scimmer.mapping.schema-card.array-attr.events
  (:require [re-frame.core :as rf]))

(def default-sub-item
  {:type       "type1"
   :collection "user"
   :mapped-to  "sub_item"})

(rf/reg-event-db
 :mapping/>add-sub-item
 [(rf/path :schema :attrs)]
 (fn [attrs [_ attr-id]]
   (let [id (random-uuid)]
     (->> (assoc default-sub-item :id id)
          (assoc-in attrs [attr-id :sub-items id])))))

(rf/reg-event-db
 :mapping/>add-ext-sub-item
 [(rf/path :schema :extensions)]
 (fn [exts [_ ext-id attr-id]]
   (assoc-in exts [ext-id :attrs attr-id :sub-items (random-uuid)] default-sub-item)))

(rf/reg-event-db
 :mapping/>remove-sub-item
 [(rf/path :schema :attrs)]
 (fn [attrs [_ attr-id sub-item-id]]
   (update-in attrs [attr-id :sub-items] dissoc sub-item-id)))

(rf/reg-event-db
 :mapping/>remove-ext-sub-item
 [(rf/path :schema :extensions)]
 (fn [exts [_ ext-id attr-id sub-item-id]]
   (update-in exts [ext-id :attrs attr-id :sub-items] dissoc sub-item-id)))

(rf/reg-event-db
 :mapping/>set-array-type
 [(rf/path :schema :attrs)]
 (fn [attrs [_ attr-id sub-item-id type]]
   (assoc-in attrs [attr-id :sub-items sub-item-id :type] type)))

(rf/reg-event-db
 :mapping/>set-ext-array-type
 [(rf/path :schema :extensions)]
 (fn [exts [_ ext-id attr-id sub-item-id type]]
   (assoc-in exts [ext-id :attrs attr-id :sub-items sub-item-id :type] type)))

(rf/reg-event-db
 :mapping/>set-array-mapped-to
 [(rf/path :schema :attrs)]
 (fn [attrs [_ attr-id sub-item-id mapped-to]]
   (assoc-in attrs [attr-id :sub-items sub-item-id :mapped-to] mapped-to)))

(rf/reg-event-db
 :mapping/>set-ext-array-mapped-to
 [(rf/path :schema :extensions)]
 (fn [exts [_ ext-id attr-id sub-item-id mapped-to]]
   (assoc-in exts [ext-id :attrs attr-id :sub-items sub-item-id :mapped-to] mapped-to)))

(rf/reg-event-db
 :mapping/>set-array-collection
 [(rf/path :schema :attrs)]
 (fn [attrs [_ attr-id sub-item-id collection]]
   (assoc-in attrs [attr-id :sub-items sub-item-id :collection] collection)))

(rf/reg-event-db
 :mapping/>set-ext-array-collection
 [(rf/path :schema :extensions)]
 (fn [exts [_ ext-id attr-id sub-item-id collection]]
   (assoc-in exts [ext-id :attrs attr-id :sub-items sub-item-id :collection] collection)))

(def default-array-attr
  {:type      :array
   :name      "newArrayAttr"
   :sub-items {}})

(rf/reg-event-db
 :mapping/>add-array-attr
 [(rf/path :schema :attrs)]
 (fn [attrs _]
   (let [id (random-uuid)]
     (->> (assoc default-array-attr :id id)
          (assoc attrs id)))))

(rf/reg-event-db
 :mapping/>add-ext-array-attr
 [(rf/path :schema :extensions)]
 (fn [exts [_ ext-id]]
   (assoc-in exts [ext-id :attrs (random-uuid)] default-array-attr)))

