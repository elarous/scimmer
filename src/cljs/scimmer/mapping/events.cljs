(ns scimmer.mapping.events
  (:require
   [re-frame.core :as rf]
   [ajax.core :as ajax]
   [cognitect.transit :as transit]
   [scimmer.services.mapping :refer [build-resource]]))

(def reader (transit/reader :json))

(rf/reg-event-db
 :mapping/>resource->entities
 (fn [db [_ _]]
   (try
     (let [schema (:mapping db)
           resource (:resource db)]
       (assoc db :entities (build-resource (:attrs schema) resource [] {} {})))
     (catch js/Error e (js/console.log e)))))

(rf/reg-event-fx
 :mapping/>load-schemas!
 (fn [_ _]
   {:http-xhrio {:method          :get
                 :uri             "http://localhost:3003/api/schemas"
                 :timeout         8000
                 :response-format (ajax/transit-response-format)
                 :on-success      [:mapping/>confirm-load-schemas]
                 :on-failure      [:mapping/>reject-load-schemas]}}))

(rf/reg-event-fx
 :mapping/>confirm-load-schemas
 (fn [_ [_ schemas]]
   {:dispatch [:mapping/>load-schema! (->> schemas first :id)]}))

(rf/reg-event-db
 :mapping/>reject-load-schemas
 (fn [db [_ error]]
   (js/console.log error)
   db))

(rf/reg-event-fx
 :mapping/>load-schema!
 (fn [_ [_ id]]
   {:http-xhrio {:method          :get
                 :uri             (str "http://localhost:3003/api/schemas/" id)
                 :timeout         8000
                 :response-format (ajax/transit-response-format)
                 :on-success      [:mapping/>confirm-load-schema]
                 :on-failure      [:mapping/>reject-load-schema]}}))

;; TODO: move this helper functions somewhere

(defn index-by [f v]
  (->> (map (fn [item] [(f item) item]) v)
       (into {})))

;;

(rf/reg-event-db
 :mapping/>confirm-load-schema
 (fn [db [_ resp]]
   (js/console.log resp)
   (let [index-by-uuid (partial index-by :id)
         index-attrs (fn [attrs] (map #(cond
                                         (some? (:sub-attrs %))
                                         (update % :sub-attrs index-by-uuid)
                                         (some? (:sub-items %))
                                         (update % :sub-items index-by-uuid)
                                         :else %) attrs))
         _ (def resp resp)
         extensions    (->> (:extensions resp)
                            (map (fn [ext]
                                   (update ext
                                          :attrs
                                          (comp index-by-uuid index-attrs))))
                            index-by-uuid)

         attrs (->> (:attrs resp)
                    index-attrs
                    index-by-uuid)

         schema        (-> resp
                           (assoc :attrs attrs)
                           (assoc :extensions extensions))]
     (js/console.log schema)
     (assoc db :schema schema))))

(comment

  resp 


  ,)

(rf/reg-event-db
 :mapping/>reject-load-schema
 (fn [db [_ error]]
   (js/console.log error)
   db))


