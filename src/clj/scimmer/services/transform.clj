(ns scimmer.services.transform
  (:require [ring.util.http-response :as resp]
            [scimmer.services.schemas :as schemas]
            [scimmer.services.mapping :as mapping]
            [scimmer.services.mapping-utils :as map-utils])
  (:import java.util.UUID))

(defn- wrap-resource->entities [entities schema-id]
  {:meta {:schema-id schema-id}
   :entities entities})

(defn resource->entities [req]
  (let [{:keys [schema-id resource]} (:body-params req)
        schema (schemas/find-schema! (UUID/fromString schema-id))
        malli-schema (map-utils/combine-malli-schema schema)
        entities (mapping/build-resource malli-schema resource [] {} {})]
    (-> entities
        (wrap-resource->entities schema-id)
        resp/ok)))

