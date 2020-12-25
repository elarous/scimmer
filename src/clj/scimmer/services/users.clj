(ns scimmer.services.users
  (:require [ring.util.http-response :refer :all]
            [clojure.pprint :as pp]
            [scimmer.services.mapping :as m]
            [scimmer.services.schema :as sch]
            [scimmer.services.persistance :as p]))

(defn get-users [request]
  (ok {:users []}))

(defn get-user [{:keys [path-params]}]
  (println "User : " (:id path-params))
  ;; get resources from DB
  ;; -> need a configuration to know how to join tables
  ;; Make entities in the mapping shape
  ;; combine them into a SCIM resource
  ;; apply the update patches
  (ok {:ok "ok"}))
(defn create-user [{:keys [body-params]}]
  (let [entities-maps (m/map-resource->entities body-params m/mapping)]
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
