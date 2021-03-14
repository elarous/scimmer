(ns scimmer.mapping.schema-card.events
  (:require
   [re-frame.core :as rf]
   [ajax.core :as ajax]))

(defn unindex [m]
  (->> (vals m)
       (map (fn [attr]
              (cond
                (map? (:sub-attrs attr))
                (update attr :sub-attrs vals)
                (map? (:sub-items attr))
                (update attr :sub-items vals)
                :else attr)))))

(defn unindex-schema [db]
  (-> db
      (update-in [:schema :attrs] unindex)
      (update-in [:schema :extensions] unindex)
      (update-in [:schema :extensions] (fn [exts]
                                         (map #(update % :attrs unindex) exts)))))

; Events
(rf/reg-event-db
 :mapping/>set-attr
 [(rf/path :schema)]
 (fn [schema [_ id name]]
   (assoc-in schema [:attrs id :name] name)))

(rf/reg-event-db
 :mapping/>remove-attr
 [(rf/path :schema)]
 (fn [schema [_ id]]
   (update schema :attrs dissoc id)))

(rf/reg-event-fx
 :mapping/>save
 (fn [{db :db} _]
   (let [new-schema
         (-> (:schema db)
             (update :attrs unindex)
             (update :extensions unindex)
             (update :extensions (fn [exts] (map #(update % :attrs unindex) exts))))]
     {:http-xhrio {:method          :post
                   :uri             (str "http://localhost:3003/api/schemas/" (:id new-schema))
                   :timeout         8000
                   :format          (ajax/transit-request-format)
                   :response-format (ajax/transit-response-format)
                   :on-success      [:mapping/>confirm-save]
                   :on-failure      [:mapping/>reject-save]
                   :params          new-schema}})))

(rf/reg-event-fx
 :mapping/>confirm-save
 (fn [{db :db} _]
   (js/console.log "Saving schema succeeded!")
   {:db       (assoc db :schema-saved? true)
    :dispatch [:mapping/>refresh-schemas!]}))

(rf/reg-event-db
 :mapping/>reject-save
 (fn [db _]
   (js/console.error "Saving schema failed!")
   db))

