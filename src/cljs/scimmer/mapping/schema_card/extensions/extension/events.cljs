(ns scimmer.mapping.schema-card.extensions.extension.events
  (:require [re-frame.core :as rf]))

(rf/reg-event-db
  :mapping/>remove-ext
  [(rf/path :extensions)]
  (fn [exts [_ ext-id]]
    (dissoc exts ext-id)))

(def default-ext
  {:label "New Extension"
   :attrs {}})

(rf/reg-event-db
  :mapping/>add-ext
  [(rf/path :extensions)]
  (fn [exts _]
    (assoc exts (random-uuid) default-ext)))

(rf/reg-event-db
  :mapping/>set-ext-label
  [(rf/path :extensions)]
  (fn [exts [_ ext-id label]]
    (assoc-in exts [ext-id :label] label)))

