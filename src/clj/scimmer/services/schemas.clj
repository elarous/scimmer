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

(defn- unqualify-keys [m]
  (->> (map (fn [[k v]] [(-> k name keyword) v]) m)
       (into {})))

;; queries
(defn- save-schema-query [schema]
  (let [schema-data (select-keys schema [:resource :name :is_default])]
    (-> (insert-into :schemas)
        (values [schema-data])
        (hp/upsert (-> (hp/on-conflict :id)
                       (hp/do-update-set :resource :name :is_default)))
        (hp/returning :*))))

(defn- save-extensions-query [schema-id extensions]
  (let [exts-data (map #(-> (select-keys % [:id :name])
                            (assoc :schema_id schema-id)) extensions)]
    (-> (insert-into :extensions)
        (values exts-data)
        (hp/upsert (-> (hp/on-conflict :id)
                       (hp/do-update-set :name)))
        (hp/returning :*))))

(defn- save-single-query [container-id single-attrs & {:keys [extension?]}]
  (let [container-k (if extension? :extension_id :schema_id)
        data (map (fn [sa]
                    (-> sa
                        (select-keys [:id :name :mapped_to :collection])
                        (assoc container-k container-id)))
                  single-attrs)]
    (-> (insert-into :single_attrs)
        (values data)
        (hp/upsert (-> (hp/on-conflict :id)
                       (hp/do-update-set :name :mapped_to :collection container-k)))
        (hp/returning :*))))

(defn- save-object-query [container-id object-attrs & {:keys [extension?]}]
  (let [container-k (if extension? :extension_id :schema_id)
        data (map (fn [oa]
                    (-> oa
                        (select-keys [:id :name])
                        (assoc container-k container-id))) object-attrs)]
    (-> (insert-into :object_attrs)
        (values data)
        (hp/upsert (-> (hp/on-conflict :id)
                       (hp/do-update-set :name container-k)))
        (hp/returning :*))))

(defn- save-array-query [container-id array-attrs & {:keys [extension?]}]
  (let [container-k (if extension? :extension_id :schema_id)
        data (map (fn [aa]
                    (-> aa
                        (select-keys [:id :name])
                        (assoc container-k container-id))) array-attrs)]
    (-> (insert-into :array_attrs)
        (values data)
        (hp/upsert (-> (hp/on-conflict :id)
                       (hp/do-update-set :name container-k)))
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

(defn- save-single-attrs! [schema-id single-attrs & {:keys [extension?]}]
  (let [updated-attrs (exec! (save-single-query schema-id single-attrs :extension? extension?))]
    (map #(-> % unqualify-keys (assoc :type :single)) updated-attrs)))

(defn- save-object-attrs! [schema-id object-attrs & {:keys [extension?]}]
  (let [updated-attrs (exec! (save-object-query schema-id object-attrs :extension? extension?))]
    (map #(-> % unqualify-keys (assoc :type :object)) updated-attrs)))

(defn- save-array-attrs! [schema-id array-attrs & {:keys [extension?]}]
  (let [updated-attrs (exec! (save-array-query schema-id array-attrs :extension? extension?))]
    (map #(-> % unqualify-keys (assoc :type :array)) updated-attrs)))

(defn- save-sub-attrs! [sub-attrs]
  (let [updated-sub-attrs (exec! (save-sub-attr-query sub-attrs))]
    (map unqualify-keys updated-sub-attrs)))

(defn- save-sub-items! [sub-items]
  (let [updated-sub-items (exec! (save-sub-item-query sub-items))]
    (map unqualify-keys updated-sub-items)))

(defn- objs+sub-attrs [objs sub-attrs]
  (let [grouped-attrs (group-by :object_attr_id sub-attrs)]
    (map (fn [obj] (assoc obj :sub-attrs (get grouped-attrs (:id obj)))) objs)))

(defn- arrs+sub-items [arrs sub-attrs]
  (let [grouped-attrs (group-by :array_attr_id sub-attrs)]
    (map (fn [arr] (assoc arr :sub-items (get grouped-attrs (:id arr)))) arrs)))

(defn upsert-attrs! [schema-id attrs & {:keys [extension?]}]
  (let [singles (filter #(= :single (:type %)) attrs)
        objects (filter #(= :object (:type %)) attrs)
        arrays (filter #(= :array (:type %)) attrs)
        sub-attrs (apply concat (map (fn [obj]
                                       (map #(assoc % :object_attr_id (:id obj)) (:sub-attrs obj))) objects))
        sub-items (apply concat (map (fn [arr]
                                       (map #(assoc % :array_attr_id (:id arr)) (:sub-items arr))) arrays))]
    (concat
     (save-single-attrs! schema-id singles :extension? extension?)
     (objs+sub-attrs
      (save-object-attrs! schema-id objects :extension? extension?)
      (save-sub-attrs! sub-attrs))
     (arrs+sub-items
      (save-array-attrs! schema-id arrays :extension? extension?)
      (save-sub-items! sub-items)))))

(defn upsert-extensions! [schema-id exts]
  (let [updated-exts (->> (save-extensions-query schema-id exts)
                          exec!
                          (map unqualify-keys))
        updated-attrs (flatten (map #(upsert-attrs! (:id %) (:attrs %) :extension? true) exts))
        grouped-attrs (group-by :extension_id updated-attrs)]
    (map #(assoc % :attrs (get grouped-attrs (:id %))) updated-exts)))

(defn upsert-schema! [schema]
  (let [updated-schema  (-> (save-schema-query schema)
                            exec!
                            first
                            unqualify-keys)
        updated-attrs (upsert-attrs! (:id updated-schema) (:attrs schema))
        extensions (upsert-extensions! (:id updated-schema) (:extensions schema))]
    (-> updated-schema
        (assoc :attrs updated-attrs)
        (assoc :extensions extensions))))

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
                           :collection     "user"}]}]
     :extensions
      [{:id #uuid "11100000-061d-45f1-8d92-005627a156f9"
        :name "first extension"
        :attrs [{:type      :single
                  :id #uuid "11223344-061d-45f1-8d92-db5627a156f2"
                  :name      "id"
                  :mapped_to "uuid"
                  :collection     "user"}
                {:id #uuid "22baaaaa-061d-45f1-8d92-db5627a156f2"
                  :type      :object
                  :name      "name"
                  :sub-attrs [{:id #uuid "03333aaa-061d-45f1-8d92-db5627a156f2"
                              :name      "familyName"
                              :mapped_to "last_name"
                              :collection     "user"}]}
                {:type      :array
                  :id #uuid  "0bb44bbb-061d-45f1-8d92-db5627a156f2"
                  :name      "phoneNumbers"
                  :sub-items [{:id #uuid "de72a3bd-4ce3-4ea0-b90b-337d4fbebc5f"
                              :mapped_to "mobile_phone"
                              :type      "mobile"
                              :collection     "user"}]}]}]})

  (upsert-schema! data))

