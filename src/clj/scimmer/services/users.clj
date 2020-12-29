(ns scimmer.services.users
  (:require [ring.util.http-response :refer :all]
            [clojure.pprint :as pp]
            [malli.util :as mu]
            [scimmer.services.mapping2 :as mapping]
            [scimmer.services.schema :as sch]
            [scimmer.services.persistance :as p]
            [scimmer.services.resource :as resource]))

;; helpers
(def entities->user-resource
  (partial resource/build-resource (mu/to-map-syntax sch/user-schema)))

(def user-resource->entities
  (partial mapping/build-resource (mu/to-map-syntax sch/user-schema)))
;;

(defn get-user [{:keys [path-params]}]
  (let [entities (p/get-user {:id (:id path-params)})]
    (ok (entities->user-resource entities))))

(defn get-users [_request]
  (let [entities-list (p/get-users)]
    (ok (map entities->user-resource entities-list))))

(defn create-user [{:keys [body-params]}]
  (let [entities-maps (user-resource->entities body-params [] {} {})
        saved-entities (p/create-user entities-maps)]
    (ok (entities->user-resource saved-entities))))

;; TODO: this needs the resource (user + other kinds) to be retrievable from the database so that we can apply patch updates on it
(defn update-user [{:keys [path-params body-params]}]
  (let [patch-schema (sch/malli->scim-patch sch/user-schema)
        #_find-user!]
    (pp/pprint patch-schema)
    {:message "ok"}))

(comment
  (user-resource->entities user [] {} {})
  (get-user {:path-params {:id 1659}})
  (def my-data {:data "name"})
  (meta m/mapping)
  (sch/malli->scim-patch sch/full-user)
  (sch/malli->scim-patch sch/enterprise-ext)
  (sch/malli->scim-patch sch/user-schema))
