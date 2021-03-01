(ns scimmer.mapping.schema-card.extensions.extension.events
  (:require [re-frame.core :as rf]))

(rf/reg-event-db
 :mapping/>remove-ext
 [(rf/path :schema :extensions)]
 (fn [exts [_ ext-id]]
   (dissoc exts ext-id)))

(def default-ext
  {:name "New Extension"
   :attrs {}})

(rf/reg-event-db
 :mapping/>add-ext
 [(rf/path :schema :extensions)]
 (fn [exts _]
   (let [id (random-uuid)]
     (->> (assoc default-ext :id id)
          (assoc exts id)))))

(rf/reg-event-db
 :mapping/>set-ext-name
 [(rf/path :schema :extensions)]
 (fn [exts [_ ext-id name]]
   (assoc-in exts [ext-id :name] name)))

