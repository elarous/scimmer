(ns scimmer.services.schemas
  (:require [honeysql.core :as sql]
            [honeysql.helpers :as h]
            [honeysql-postgres.helpers :as hp]
            [honeysql-postgres.format]
            [next.jdbc :as jdbc]
            [camel-snake-kebab.core :as csk]
            [camel-snake-kebab.extras :as cske]
            [scimmer.db.core :refer [*db*]]))

;; helpers
(defn- exec! [query]
  (if-let [db *db*]
    (jdbc/execute! db (sql/format query))
    (throw (Error. "No db connection found when trying to run query"))))

(defn- unqualify-keys [m]
  (->> (map (fn [[k v]] [(-> k name keyword) v]) m)
       (into {})
       (cske/transform-keys csk/->kebab-case-keyword)))

(defn- snake-case-keys [m]
  (cske/transform-keys csk/->snake_case_keyword m))

;; queries
(defn- save-schema-query [schema]
  (let [schema-data (select-keys schema [:id :resource :name :is_default])]
    (-> (h/insert-into :schemas)
        (h/values [schema-data])
        (hp/upsert (-> (hp/on-conflict :id)
                       (hp/do-update-set :resource :name :is_default)))
        (hp/returning :*))))

(defn- save-extensions-query [schema-id extensions]
  (let [exts-data (map #(-> (select-keys % [:id :name])
                            (assoc :schema_id schema-id)) extensions)]
    (-> (h/insert-into :extensions)
        (h/values exts-data)
        (hp/upsert (-> (hp/on-conflict :id)
                       (hp/do-update-set :name)))
        (hp/returning :*))))

(defn remove-extensions-query
  "query to remove extensions (with their children) except the ones in `kept-extensions-ids`"
  [schema-id kept-extensions-ids]
  (when schema-id
    (let [query (-> (h/delete-from :extensions)
                    (h/where [:in :schema_id [schema-id]]))]
      (if (seq kept-extensions-ids)
        (-> query
            (h/merge-where [:not [:in :id kept-extensions-ids]]))
        query))))

(defn- save-single-query [container-id single-attrs & {:keys [extension?]}]
  (let [container-k (if extension? :extension_id :schema_id)
        data        (map (fn [sa]
                           (-> sa
                               (select-keys [:id :name :mapped-to :collection])
                               (snake-case-keys)
                               (assoc container-k container-id)))
                         single-attrs)]
    (-> (h/insert-into :single_attrs)
        (h/values data)
        (hp/upsert (-> (hp/on-conflict :id)
                       (hp/do-update-set :name :mapped_to :collection container-k)))
        (hp/returning :*))))

(defn remove-single-query
  "query to remove singles except the ones in `kept-singles-ids`"
  [container-id kept-singles-ids & {:keys [extension?]}]
  (let [container-k (if extension? :extension_id :schema_id)]
    (when container-id
      (let [query (-> (h/delete-from :single_attrs)
                      (h/where [:in container-k [container-id]]))]
        (if (seq kept-singles-ids)
          (-> query
              (h/merge-where [:not [:in :id kept-singles-ids]]))
          query)))))

(defn- save-object-query [container-id object-attrs & {:keys [extension?]}]
  (let [container-k (if extension? :extension_id :schema_id)
        data        (map (fn [oa]
                           (-> oa
                               (select-keys [:id :name])
                               (snake-case-keys)
                               (assoc container-k container-id))) object-attrs)]
    (-> (h/insert-into :object_attrs)
        (h/values data)
        (hp/upsert (-> (hp/on-conflict :id)
                       (hp/do-update-set :name container-k)))
        (hp/returning :*))))

(defn remove-object-query
  "query to remove objects (with their sub-attrs) except the ones in `kept-objects-ids`"
  [container-id kept-objects-ids & {:keys [extension?]}]
  (let [container-k (if extension? :extension_id :schema_id)]
    (when container-id
      (let [query (-> (h/delete-from :object_attrs)
                      (h/where [:in container-k [container-id]]))]
        (if (seq kept-objects-ids)
          (-> query
              (h/merge-where [:not [:in :id kept-objects-ids]]))
          query)))))

(defn- save-array-query [container-id array-attrs & {:keys [extension?]}]
  (let [container-k (if extension? :extension_id :schema_id)
        data        (map (fn [aa]
                           (-> aa
                               (select-keys [:id :name])
                               (snake-case-keys)
                               (assoc container-k container-id))) array-attrs)]
    (-> (h/insert-into :array_attrs)
        (h/values data)
        (hp/upsert (-> (hp/on-conflict :id)
                       (hp/do-update-set :name container-k)))
        (hp/returning :*))))

(defn remove-array-query
  "query to remove arrays (with their sub-items) except the ones in `kept-arrays-ids`"
  [container-id kept-arrays-ids & {:keys [extension?]}]
  (let [container-k (if extension? :extension_id :schema_id)]
    (when container-id
      (let [query (-> (h/delete-from :array_attrs)
                      (h/where [:in container-k [container-id]]))]
        (if (seq kept-arrays-ids)
          (-> query
              (h/merge-where [:not [:in :id kept-arrays-ids]]))
          query)))))

(defn save-sub-attr-query [sub-attrs]
  (let [data (map #(-> %
                       (select-keys [:id :name :mapped-to :collection :object-attr-id])
                       (snake-case-keys)) sub-attrs)]
    (-> (h/insert-into :sub_attrs)
        (h/values data)
        (hp/upsert (-> (hp/on-conflict :id)
                       (hp/do-update-set :name :mapped_to :collection :object_attr_id)))
        (hp/returning :*))))

(defn remove-sub-attrs-query
  "query to remove all sub-attrs except the ones in `kept-sub-attrs-ids`"
  [object-ids kept-sub-attrs-ids]
  (when (seq object-ids)
    (let [query (-> (h/delete-from :sub_attrs)
                    (h/where [:in :object_attr_id object-ids]))]
      (if (seq kept-sub-attrs-ids)
        (-> query
            (h/merge-where [:not [:in :id kept-sub-attrs-ids]]))
        query))))

(defn save-sub-item-query [sub-items]
  (let [data (map #(-> %
                       (select-keys [:id :type :mapped-to :collection :array-attr-id])
                       (snake-case-keys)) sub-items)]
    (-> (h/insert-into :sub_items)
        (h/values data)
        (hp/upsert (-> (hp/on-conflict :id)
                       (hp/do-update-set :type :mapped_to :collection :array_attr_id)))
        (hp/returning :*))))

(defn remove-sub-items-query
  "query to remove all sub-items except the ones in `kept-sub-items-ids`"
  [array-ids kept-sub-items-ids]
  (when (seq array-ids)
    (let [query (-> (h/delete-from :sub_items)
                    (h/where [:in :array_attr_id array-ids]))]
      (if (seq kept-sub-items-ids)
        (-> query
            (h/merge-where [:not [:in :id kept-sub-items-ids]]))
        query))))

;; end queries

(defn- save-single-attrs! [schema-id single-attrs & {:keys [extension?]}]
  (let [updated-attrs (when (seq single-attrs)
                        (exec! (save-single-query schema-id single-attrs :extension? extension?)))]
    (some-> (remove-single-query schema-id (map :id single-attrs) :extension? extension?) exec!)
    (map #(-> % unqualify-keys (assoc :type :single)) updated-attrs)))

(defn- save-object-attrs! [schema-id object-attrs & {:keys [extension?]}]
  (let [updated-attrs (when (seq object-attrs)
                        (exec! (save-object-query schema-id object-attrs :extension? extension?)))]
    (some-> (remove-object-query schema-id (map :id object-attrs) :extension? extension?) exec!)
    (map #(-> % unqualify-keys (assoc :type :object)) updated-attrs)))

(defn- save-array-attrs! [schema-id array-attrs & {:keys [extension?]}]
  (let [updated-attrs (when (seq array-attrs)
                        (exec! (save-array-query schema-id array-attrs :extension? extension?)))]
    (some-> (remove-array-query schema-id (map :id array-attrs) :extension? extension?) exec!)
    (map #(-> % unqualify-keys (assoc :type :array)) updated-attrs)))

(defn save-sub-attrs! [sub-attrs object-ids]
  (let [updated-sub-attrs (when (seq sub-attrs) (exec! (save-sub-attr-query sub-attrs)))]
    (some-> (remove-sub-attrs-query object-ids (map :id sub-attrs)) exec!)
    (map unqualify-keys updated-sub-attrs)))

(defn- save-sub-items! [sub-items array-ids]
  (let [updated-sub-items (when (seq sub-items) (exec! (save-sub-item-query sub-items)))]
    (some-> (remove-sub-items-query array-ids (map :id sub-items)) exec!)
    (map unqualify-keys updated-sub-items)))

(defn- objs+sub-attrs [objs sub-attrs]
  (let [grouped-attrs (group-by :object-attr-id sub-attrs)]
    (map (fn [obj] (assoc obj :sub-attrs (get grouped-attrs (:id obj)))) objs)))

(defn- arrs+sub-items [arrs sub-attrs]
  (let [grouped-attrs (group-by :array-attr-id sub-attrs)]
    (map (fn [arr] (assoc arr :sub-items (get grouped-attrs (:id arr)))) arrs)))

(defn upsert-attrs! [schema-id attrs & {:keys [extension?]}]
  (let [singles   (filter #(= :single (:type %)) attrs)
        objects   (filter #(= :object (:type %)) attrs)
        arrays    (filter #(= :array (:type %)) attrs)
        sub-attrs (apply concat (map (fn [obj]
                                       (map #(assoc % :object-attr-id (:id obj)) (:sub-attrs obj))) objects))
        sub-items (apply concat (map (fn [arr]
                                       (map #(assoc % :array-attr-id (:id arr)) (:sub-items arr))) arrays))]
    (concat
     (save-single-attrs! schema-id singles :extension? extension?)
     (objs+sub-attrs
      (save-object-attrs! schema-id objects :extension? extension?)
      (save-sub-attrs! sub-attrs (map :id objects)))
     (arrs+sub-items
      (save-array-attrs! schema-id arrays  :extension? extension?)
      (save-sub-items! sub-items (map :id arrays))))))

(defn upsert-extensions! [schema-id exts]
  (if (seq exts)
    (let [updated-exts  (->> (save-extensions-query schema-id exts)
                             exec!
                             (map unqualify-keys))
          updated-attrs (flatten (map #(upsert-attrs! (:id %) (:attrs %) :extension? true) exts))
          grouped-attrs (group-by :extension-id updated-attrs)]
      (some-> (remove-extensions-query schema-id (map :id exts)) exec!)
      (map #(assoc % :attrs (get grouped-attrs (:id %))) updated-exts))
    (some-> (remove-extensions-query schema-id (map :id exts)) exec!)))

(defn upsert-schema! [schema]
  (let [updated-schema (-> (save-schema-query schema)
                           exec!
                           first
                           unqualify-keys)
        updated-attrs  (upsert-attrs! (:id updated-schema) (:attrs schema))
        extensions     (upsert-extensions! (:id updated-schema) (:extensions schema))]
    (-> updated-schema
        (assoc :attrs updated-attrs)
        (assoc :extensions extensions))))

(defn all! []
  (let [result (-> (h/select :*)
                   (h/from :schemas)
                   exec!)]
    (map unqualify-keys result)))

(defn- compose-attrs [attrs]
  (let [grouped-sub-attrs (group-by :object-attr-id (:sub-attrs attrs))
        grouped-sub-items (group-by :array-attr-id (:sub-items attrs))
        single-attrs      (map #(assoc % :type :single) (:single-attrs attrs))
        object-attrs      (map
                           (fn [object-attr]
                             (-> object-attr
                                 (assoc :sub-attrs (get grouped-sub-attrs (:id object-attr)))
                                 (assoc :type :object)))
                           (:object-attrs attrs))
        array-attrs       (map
                           (fn [array-attr]
                             (-> array-attr
                                 (assoc :sub-items (get grouped-sub-items (:id array-attr)))
                                 (assoc :type :array)))
                           (:array-attrs attrs))]
    (concat single-attrs object-attrs array-attrs)))

(defn- set-attrs [attrs]
  (assoc (first (:containers attrs)) :attrs (compose-attrs attrs)))

(defn- find-helper! [type container-id]
  (let [[table-k id-k]   (if (= type :schema)
                           [:schemas :schema_id]
                           [:extensions :extension_id])
        get-data         #(->> % exec! (map unqualify-keys))
        table-q          (-> (h/select :*)
                             (h/from table-k)
                             (h/where [:= :id container-id]))
        single-attrs-q   (-> (h/select :*)
                             (h/from :single_attrs)
                             (h/where [:= id-k container-id]))
        object-attrs-q   (-> (h/select :*)
                             (h/from :object_attrs)
                             (h/where [:= id-k container-id]))
        array-attrs-q    (-> (h/select :*)
                             (h/from :array_attrs)
                             (h/where [:= id-k container-id]))
        table-containers (get-data table-q)
        single-attrs     (get-data single-attrs-q)
        object-attrs     (get-data object-attrs-q)
        array-attrs      (get-data array-attrs-q)
        sub-attrs-q      (-> (h/select :*)
                             (h/from :sub_attrs)
                             (h/where [:in :object_attr_id (map :id object-attrs)]))
        sub-items-q      (-> (h/select :*)
                             (h/from :sub_items)
                             (h/where [:in :array_attr_id (map :id array-attrs)]))
        sub-attrs        (when (seq object-attrs) (get-data sub-attrs-q))
        sub-items        (when (seq array-attrs) (get-data sub-items-q))]
    {:containers   table-containers
     :single-attrs single-attrs
     :object-attrs object-attrs
     :array-attrs  array-attrs
     :sub-attrs    sub-attrs
     :sub-items    sub-items}))

(defn find-extensions! [schema-id]
  (let [extensions-q (-> (h/select :id)
                         (h/from :extensions)
                         (h/where [:= :schema_id schema-id]))
        extensions   (map unqualify-keys (exec! extensions-q))]
    (->> (map :id extensions)
         (map (partial find-helper! :extension))
         (map set-attrs))))

(defn find-schema! [schema-id]
  (let [attrs-data (find-helper! :schema schema-id)
        schema     (set-attrs attrs-data)
        extensions (find-extensions! schema-id)]
    (assoc schema :extensions extensions)))

;; service functions

(defn get-all [_request]
  (all!))

(defn get-schema [request]
  (let [id (get-in request [:path-params :id])]
    (find-schema! (java.util.UUID/fromString id))))

(defn save-schema [request]
  (let [schema  (:body-params request)]
    (upsert-schema! schema)))

;;

(comment
  (tap>
   (find-schema! #uuid "0ea136ad-061d-45f1-8d92-005627a156f9"))

  (tap>
   (find-extensions! #uuid "7a224f1a-f80f-4238-b89c-56aec5b6f784"))

  (tap>
   (find-helper! :extension  #uuid "11100000-061d-45f1-8d92-005627a156f9"))

  (defn find! [schema-id]
    (find-helper! :schema schema-id)))

(comment
  (def data
    {:id         #uuid "0ea136ad-061d-45f1-8d92-005627a156f9"
     :resource   "user"
     :name       "first_schema"
     :is-default false
     :attrs      [{:type       :single
                   :id         #uuid "0ea136ad-061d-45f1-8d92-db5627a156f2"
                   :name       "id"
                   :mapped-to  "uuid"
                   :collection "user"}
                  {:type       :single
                   :id         #uuid "11122333-061d-45f1-8d92-db5627a156f2"
                   :name       "userName"
                   :mapped-to  "user_name"
                   :collection "user"}
                  {:id        #uuid "aaaaaaa-061d-45f1-8d92-db5627a156f2"
                   :type      :object
                   :name      "name"
                   :sub-attrs [{:id         #uuid "0aaaaaaa-061d-45f1-8d92-db5627a156f2"
                                :name       "familyName"
                                :mapped-to  "last_name"
                                :collection "user"}]}
                  {:type      :array
                   :id        #uuid  "0bbbabbb-061d-45f1-8d92-db5627a156f2"
                   :name      "phoneNumbers"
                   :sub-items [{:id         #uuid "de72b3bd-4ce3-4ea0-b90b-337d4fbebc5f"
                                :mapped-to  "mobile_phone"
                                :type       "mobile"
                                :collection "user"}]}]
     :extensions
     [{:id    #uuid "11100000-061d-45f1-8d92-005627a156f9"
       :name  "first extension"
       :attrs [{:type       :single
                :id         #uuid "11223344-061d-45f1-8d92-db5627a156f2"
                :name       "id"
                :mapped-to  "uuid"
                :collection "user"}
               {:id        #uuid "22baaaaa-061d-45f1-8d92-db5627a156f2"
                :type      :object
                :name      "name"
                :sub-attrs [{:id         #uuid "03333aaa-061d-45f1-8d92-db5627a156f2"
                             :name       "familyName"
                             :mapped-to  "last_name"
                             :collection "user"}]}
               {:type      :array
                :id        #uuid  "0bb44bbb-061d-45f1-8d92-db5627a156f2"
                :name      "phoneNumbers"
                :sub-items [{:id         #uuid "de72a3bd-4ce3-4ea0-b90b-337d4fbebc5f"
                             :mapped-to  "mobile_phone"
                             :type       "mobile"
                             :collection "user"}]}]}]})

  (tap>
   (upsert-schema! data)))


