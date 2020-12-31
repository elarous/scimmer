(ns scimmer.services.persistance
  (:require [honeysql.core :as sql]
            [honeysql.helpers :refer :all :as helpers]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as jdbc-sql]
            [clj-http.client :as http]
            [clojure.data.json :as json]))

;; TODO: this should be in an env variable
(def base-url "http://localhost:3000/integration_users/")

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

(comment
  (defn run [query]
    (jdbc/execute! conn (sql/format query)))
  (insert-resource! :ok {:name "kareem" :age 32 :another :ok :hi :therke}))

