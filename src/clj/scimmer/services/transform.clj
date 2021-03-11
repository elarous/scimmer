(ns scimmer.services.transform
  (:require [ring.util.http-response :as resp]
            [scim-patch.core :as patch]
            [scimmer.services.schemas :as schemas]
            [scimmer.services.mapping :as mapping]
            [scimmer.services.resource :as rs]
            [scimmer.services.scim-patch :as sp]
            [scimmer.services.mapping-utils :as map-utils])
  (:import java.util.UUID))

(defn- wrap-resource->entities [entities schema-id]
  {:meta {:schema-id schema-id}
   :entities entities})

(defn- wrap-entities->resource [resource schema-id]
  {:meta {:schema-id schema-id}
   :resource resource})

(defn- wrap-entities->resources [resources schema-id]
  {:meta {:schema-id schema-id}
   :resources resources})

(defn- resource->entities-put [req]
  (let [{:keys [schema-id resource]} (:body-params req)
        schema (schemas/find-schema! (UUID/fromString schema-id))
        malli-schema (map-utils/combine-malli-schema schema)
        entities (mapping/build-resource malli-schema resource [] {} {})]
    (-> entities
        (wrap-resource->entities schema-id)
        resp/ok)))

(defn- resource->entities-patch [req]
  (let [{:keys [schema-id patch-req entities]} (:body-params req)
        schema (schemas/find-schema! (UUID/fromString schema-id))
        malli-schema (map-utils/combine-malli-schema schema)
        patch-schema (sp/schema->scim-patch-schema malli-schema)
        resource (rs/build-resource malli-schema entities)
        patch-operations (sp/lower-case-operations (:Operations patch-req))
        patched-resource (patch/patch patch-schema resource patch-operations)
        patched-entities (mapping/build-resource malli-schema patched-resource [] {} {})]
    (-> patched-entities
        (wrap-resource->entities schema-id)
        resp/ok)))

(defn- entities->resource-one [req]
  (let [{:keys [schema-id entities]} (:body-params req)
        schema (schemas/find-schema! (UUID/fromString schema-id))
        malli-schema (map-utils/combine-malli-schema schema)
        resource (rs/build-resource malli-schema entities)]
    (-> resource
        (wrap-entities->resource schema-id)
        (resp/ok))))

(defn- entities->resource-many [req]
  (let [{:keys [schema-id entities-list]} (:body-params req)
        schema (schemas/find-schema! (UUID/fromString schema-id))
        malli-schema (map-utils/combine-malli-schema schema)
        resources (map (partial rs/build-resource malli-schema) entities-list)]
    (-> resources
        (wrap-entities->resources schema-id)
        (resp/ok))))

(defn resource->entities [req]
  (let [params (:body-params req)]
    (if (contains? params :patch-req)
      (resource->entities-patch req)
      (resource->entities-put req))))

(defn entities->resource [req]
  (let [params (:body-params req)]
    (if (contains? params :entities)
      (entities->resource-one req)
      (entities->resource-many req))))

