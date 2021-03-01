(ns scimmer.mapping.schema-card.extensions.events
  (:require [re-frame.core :as rf]))

(rf/reg-event-db
  :mapping/>set-ext-attr
  [(rf/path :schema :extensions)]
  (fn [exts [_ ext-id id name]]
    (assoc-in exts [ext-id :attrs id :name] name)))

(rf/reg-event-db
  :mapping/>remove-ext-attr
  [(rf/path :schema :extensions)]
  (fn [exts [_ ext-id id]]
    (update-in exts [ext-id :attrs] dissoc id)))
