(ns scimmer.services.users
  (:require [ring.util.http-response :refer :all]
            [clojure.pprint :as pp]
            [malli.util :as mu]
            [scim-patch.core :as pa]
            [scimmer.services.mapping :as mapping]
            [scimmer.services.schema :as sch]
            [scimmer.services.persistance :as p]
            [scimmer.services.resource :as resource]
            [scimmer.services.scim-patch :refer [schema->scim-patch-schema]]))

;; helpers
(def entities->user-resource
  (partial resource/build-resource sch/user-schema-map))

(def user-resource->entities
  (partial mapping/build-resource sch/user-schema-map))
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

(defn update-user [{:keys [path-params body-params]}]
  (let [resource (-> {:id (:id path-params)}
                     p/get-user
                     entities->user-resource)
        patch-schema (schema->scim-patch-schema sch/user-schema-map)
        ops (->> (:Operations body-params)
                 (map #(update % :op clojure.string/lower-case)))
        updated (pa/patch patch-schema resource ops)
        entities-maps (user-resource->entities updated [] {} {})
        updated-entities (p/update-user (:id path-params) entities-maps)]
    (ok (entities->user-resource updated-entities))))

(defn delete-user [{:keys [path-params]}]
  (p/delete-user (:id path-params))
  (no-content))

