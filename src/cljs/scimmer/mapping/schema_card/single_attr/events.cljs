(ns scimmer.mapping.schema-card.single-attr.events
  (:require
   [re-frame.core :as rf]))

;; update
(rf/reg-event-db
 :mapping/>set-single-collection
 [(rf/path :schema :attrs)]
 (fn [attrs [_ id collection]]
   (assoc-in attrs [id :collection] collection)))

(rf/reg-event-db
 :mapping/>set-ext-single-collection
 [(rf/path :schema :extensions)]
 (fn [exts [_ ext-id id collection]]
   (assoc-in exts [ext-id :attrs id :collection] collection)))

(rf/reg-event-db
 :mapping/>set-single-mapped-to
 [(rf/path :schema :attrs)]
 (fn [attrs [_ id mapped-to]]
   (assoc-in attrs [id :mapped-to] mapped-to)))

(rf/reg-event-db
 :mapping/>set-ext-single-mapped-to
 [(rf/path :schema :extensions)]
 (fn [exts [_ ext-id id mapped-to]]
   (assoc-in exts [ext-id :attrs id :mapped-to] mapped-to)))

;; add
(def default-single
  {:type       :single
   :name       "newAttribute"
   :mapped-to  "new_attribute"
   :collection "user"})

(rf/reg-event-db
 :mapping/>add-single-attr
 [(rf/path :schema :attrs)]
 (fn [attrs _]
   (let [id (random-uuid)]
     (assoc attrs id (assoc default-single :id id)))))

(rf/reg-event-db
 :mapping/>add-ext-single-attr
 [(rf/path :schema :extensions)]
 (fn [exts [_ ext-id]]
   (let [id (random-uuid)]
     (->> (assoc default-single :id id)
          (assoc-in exts [ext-id :attrs id] )))))

