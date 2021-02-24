(ns scimmer.services.schemas
  (:require [honeysql.core :as sql]
            [honeysql.helpers :refer :all :as h]
            [honeysql-postgres.helpers :as hp]
            [honeysql-postgres.format]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as jdbc-sql]
            [clj-http.client :as http]
            [clojure.data.json :as json]
            [mount.core :refer [args defstate]]
            [scimmer.config :refer [env]]))

;; helpers
(defn- exec! [query]
  (if-let [db scimmer.db.core/*db*]
    (jdbc/execute! db (sql/format query))
    (throw (Error. "No db connection found when trying to run query"))))

;; queries
(defn- save-single-query [schema-id single-attrs]
  (let [data (map (fn [sa]
                    (-> sa
                        (select-keys [:id :name :mapped_to :collection])
                        (assoc :schema_id schema-id)))
                  single-attrs)]
    (-> (insert-into :single_attrs)
        (values (vec data))
        (hp/upsert (-> (hp/on-conflict :id)
                       (hp/do-update-set :name :mapped_to :collection :schema_id)))
        (hp/returning :*))))

(defn- save-object-query [schema-id object-attrs]
  (let [data (map (fn [oa]
                    (-> oa
                        (select-keys [:id :name])
                        (assoc :schema_id schema-id))) object-attrs)]
    (-> (insert-into :object_attrs)
        (values data)
        (hp/upsert (-> (hp/on-conflict :id)
                       (hp/do-update-set :name :schema_id)))
        (hp/returning :*))))

(defn- save-array-query [schema-id array-attrs]
  (let [data (map (fn [aa]
                    (-> aa
                        (select-keys [:id :name])
                        (assoc :schema_id schema-id))) array-attrs)]
    (-> (insert-into :array_attrs)
        (values data)
        (hp/upsert (-> (hp/on-conflict :id)
                       (hp/do-update-set :name :schema_id)))
        (hp/returning :*))))

(defn- save-sub-attr-query [sub-attrs]
  (let [data (map #(select-keys % [:id :name :mapped_to :collection :object_attr_id]) sub-attrs)]
    (-> (insert-into :sub_attrs)
        (values data)
        (hp/upsert (-> (hp/on-conflict :id)
                       (hp/do-update-set :name :mapped_to :collection :object_attr_id)))
        (hp/returning :*))))

(defn- save-sub-item-query [sub-items]
  (let [data (map #(select-keys % [:id :type :mapped_to :collection :array_attr_id]) sub-items)]
    (-> (insert-into :sub_items)
        (values data)
        (hp/upsert (-> (hp/on-conflict :id)
                       (hp/do-update-set :type :mapped_to :collection :array_attr_id)))
        (hp/returning :*))))
;; end queries

(def save-single-attrs! (comp exec! save-single-query))
(def save-object-attrs! (comp exec! save-object-query))
(def save-array-attrs! (comp exec! save-array-query))
(def save-sub-attrs! (comp exec! save-sub-attr-query))
(def save-sub-items! (comp exec! save-sub-item-query))

(defn upsert-attrs! [schema-id attrs]
  (let [singles (filter #(= :single (:type %)) attrs)
        objects (filter #(= :object (:type %)) attrs)
        arrays (filter #(= :array (:type %)) attrs)
        sub-attrs (apply concat (map (fn [obj]
                                       (map #(assoc % :object_attr_id (:id obj)) (:sub-attrs obj))) objects))
        sub-items (apply concat (map (fn [arr]
                                       (map #(assoc % :array_attr_id (:id arr)) (:sub-items arr))) arrays))]
    (save-single-attrs! schema-id singles)
    (save-object-attrs! schema-id objects)
    (save-array-attrs! schema-id arrays)
    (save-sub-attrs! sub-attrs)
    (save-sub-items! sub-items)))

(defn upsert-schema! [schema]
  (let [data (select-keys schema [:resource :name :is_default])]
    (-> (insert-into :schemas)
        (values [data])
        exec!
        first)))

(defn upsert-extension! [ext])

(defn all! []
  (-> (select :*)
      (from :schemas)
      run!))

(comment
  (def data
    {:id #uuid "0ea136ad-061d-45f1-8d92-005627a156f9"
     :resource "user"
     :name "first_schema"
     :is_default false
     :attrs [{:type      :single
              :id #uuid "0ea136ad-061d-45f1-8d92-db5627a156f2"
              :name      "id"
              :mapped_to "uuid"
              :collection     "user"}
             {:id #uuid "aaaaaaa-061d-45f1-8d92-db5627a156f2"
              :type      :object
              :name      "name"
              :sub-attrs [{:id #uuid "0aaaaaaa-061d-45f1-8d92-db5627a156f2"
                           :name      "familyName"
                           :mapped_to "last_name"
                           :collection     "user"}]}
             {:type      :array
              :id #uuid  "0bbbabbb-061d-45f1-8d92-db5627a156f2"
              :name      "phoneNumbers"
              :sub-items [{:id #uuid "de72b3bd-4ce3-4ea0-b90b-337d4fbebc5f"
                           :mapped_to "mobile_phone"
                           :type      "mobile"
                           :collection     "user"}]}]})

  (upsert-attrs! #uuid "e927ad6f-da2b-46a6-9550-c22b3bfcc9ce"  (:attrs data))
  )
