(ns scimmer.services.persistance
  (:require [honeysql.core :as sql]
            [honeysql.helpers :refer :all :as helpers]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as jdbc-sql]
            [clj-http.client :as http]
            [clojure.data.json :as json]
            [mount.core :refer [args defstate]]
            [scimmer.config :refer [env]]))

(defstate base-url
  :start
  (:destination-base-url env))

(defn get-user [{:keys [id]}]
  (-> (http/get (str base-url id))
      (get :body)
      (json/read-str :key-fn keyword)))

(defn get-users []
  (-> (http/get base-url)
      (get :body)
      (json/read-str :key-fn keyword)))

(defn create-user [resource]
  (-> (http/post base-url
                 {:body (json/write-str resource) :content-type :json})
      (get :body)
      (json/read-str :key-fn keyword)))

(defn update-user [id resource]
  (-> (http/put (str base-url id)
                {:body (json/write-str resource) :content-type :json})
      (get :body)
      (json/read-str :key-fn keyword)))

(defn delete-user [id]
  (http/delete (str base-url id)))
