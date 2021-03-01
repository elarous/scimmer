(ns scimmer.mapping.schema-card.object-attr.events
  (:require [re-frame.core :as rf]))

(rf/reg-event-db
 :mapping/>set-sub-attr
 [(rf/path :schema :attrs)]
 (fn [attrs [_ attr-id sub-attr-id sub-attr]]
   (assoc-in attrs [attr-id :sub-attrs sub-attr-id :name] sub-attr)))

(rf/reg-event-db
 :mapping/>set-ext-sub-attr
 [(rf/path :schema :extensions)]
 (fn [exts [_ ext-id attr-id sub-attr-id sub-attr]]
   (assoc-in exts [ext-id :attrs attr-id :sub-attrs sub-attr-id :name] sub-attr)))

(def default-sub-attr
  {:name       "subAttr"
   :mapped-to  "sub_attr"
   :collection "user"})

(rf/reg-event-db
 :mapping/>add-sub-attr
 [(rf/path :schema :attrs)]
 (fn [attrs [_ attr-id]]
   (let [id (random-uuid)]
     (assoc-in attrs [attr-id :sub-attrs id] (assoc default-sub-attr :id id)))))

(rf/reg-event-db
 :mapping/>add-ext-sub-attr
 [(rf/path :schema :extensions)]
 (fn [exts [_ ext-id attr-id]]
   (js/console.log exts ext-id attr-id)
   (let [id (random-uuid)]
     (->> (assoc default-sub-attr :id id)
          (assoc-in exts [ext-id :attrs attr-id :sub-attrs id])))))

(rf/reg-event-db
 :mapping/>remove-sub-attr
 [(rf/path :schema :attrs)]
 (fn [attrs [_ attr-id sub-attr-id]]
   (update-in attrs [attr-id :sub-attrs] dissoc sub-attr-id)))

(rf/reg-event-db
 :mapping/>remove-ext-sub-attr
 [(rf/path :schema :extensions)]
 (fn [exts [_ ext-id attr-id sub-attr-id]]
   (update-in exts [ext-id :attrs attr-id :sub-attrs] dissoc sub-attr-id)))

(rf/reg-event-db
 :mapping/>set-object-mapped-to
 [(rf/path :schema :attrs)]
 (fn [attrs [_ attr-id sub-attr-id mapped-to]]
   (assoc-in attrs [attr-id :sub-attrs sub-attr-id :mapped-to] mapped-to)))

(rf/reg-event-db
 :mapping/>set-ext-object-mapped-to
 [(rf/path :schema :extensions)]
 (fn [exts [_ ext-id attr-id sub-attr-id mapped-to]]
   (assoc-in exts [ext-id :attrs attr-id :sub-attrs sub-attr-id :mapped-to] mapped-to)))

(rf/reg-event-db
 :mapping/>set-object-collection
 [(rf/path :schema :attrs)]
 (fn [attrs [_ attr-id sub-attr-id collection]]
   (assoc-in attrs [attr-id :sub-attrs sub-attr-id :collection] collection)))

(rf/reg-event-db
 :mapping/>set-ext-object-collection
 [(rf/path :schema :extensions)]
 (fn [exts [_ ext-id attr-id sub-attr-id collection]]
   (assoc-in exts [ext-id :attrs attr-id :sub-attrs sub-attr-id :collection] collection)))

(def default-object-attr
  {:type      :object
   :name      "newObjectAttr"
   :sub-attrs {}})

(rf/reg-event-db
 :mapping/>add-object-attr
 [(rf/path :schema :attrs)]
 (fn [attrs _]
   (let [id (random-uuid)]
     (assoc attrs id (assoc default-object-attr :id id)))))

(rf/reg-event-db
 :mapping/>add-ext-object-attr
 [(rf/path :schema :extensions)]
 (fn [exts [_ ext-id]]
   (let [id (random-uuid)]
     (->> (assoc default-object-attr :id id)
          (assoc-in exts [ext-id :attrs id])))))

