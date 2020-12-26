(ns scimmer.services.users
  (:require [ring.util.http-response :refer :all]
            [clojure.pprint :as pp]
            [malli.util :as mu]
            [scimmer.services.mapping :as m]
            [scimmer.services.schema :as sch]
            [scimmer.services.persistance :as p]
            [scimmer.services.resource :as resource]))


(defn get-users [request]
  (ok {:users []}))

(defn get-user [{:keys [path-params]}]
  (let [entities (p/get-user {:id (:id path-params)})
        rs (resource/build-resource
             (mu/to-map-syntax sch/user-schema) entities)]
    (ok rs)))

(defn create-user [{:keys [body-params]}]
  (let [entities-maps (m/map-resource->entities body-params {})]
    (ok (p/insert-resources! entities-maps))))

;; TODO: this needs the resource (user + other kinds) to be retrievable from the database so that we can apply patch updates on it
(defn update-user [{:keys [path-params body-params]}]
  (let [patch-schema (sch/malli->scim-patch sch/user-schema)
        #_find-user!]
    (pp/pprint patch-schema)
    {:message "ok"}))

(comment
  (def my-data {:data "name"})
  (meta m/mapping)


  (sch/malli->scim-patch sch/full-user)
  (sch/malli->scim-patch sch/enterprise-ext)
  (sch/malli->scim-patch sch/user-schema))
