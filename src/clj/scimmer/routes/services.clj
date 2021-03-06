(ns scimmer.routes.services
  (:require
   [reitit.swagger :as swagger]
   [reitit.swagger-ui :as swagger-ui]
   [reitit.ring.coercion :as coercion]
   [reitit.coercion.spec :as spec-coercion]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.ring.middleware.multipart :as multipart]
   [reitit.ring.middleware.parameters :as parameters]
   [scimmer.middleware.formats :as formats]
   [ring.util.http-response :refer :all]
   [clojure.tools.logging :as log]
   [clojure.java.io :as io]
   [scimmer.services.users :as users]
   [scimmer.services.schemas :as schemas]
   [scimmer.services.transform :as transform]
   [reitit.core :as r]))

(defn ping-handler [request]
  (ok {:message "this is crazy ping handler"}))

(defn service-routes []
  ["/api"
   {:coercion   spec-coercion/coercion
    :muuntaja   formats/instance
    :swagger    {:id ::api}
    :middleware [;; query-params & form-params
                 parameters/parameters-middleware
                 ;; content-negotiation
                 muuntaja/format-negotiate-middleware
                 ;; encoding response body
                 muuntaja/format-response-middleware
                 ;; exception handling
                 coercion/coerce-exceptions-middleware
                 ;; decoding request body
                 muuntaja/format-request-middleware
                 ;; coercing response bodys
                 coercion/coerce-response-middleware
                 ;; coercing request parameters
                 coercion/coerce-request-middleware
                 ;; multipart
                 multipart/multipart-middleware]}

   ;; swagger documentation
   ["" {:no-doc  true
        :swagger {:info {:title       "my-api"
                         :description "https://cljdoc.org/d/metosin/reitit"}}}

    ["/swagger.json"
     {:get (swagger/create-swagger-handler)}]

    ["/api-docs/*"
     {:get (swagger-ui/create-swagger-ui-handler
            {:url    "/api/swagger.json"
             :config {:validator-url nil}})}]]
   [["/resource_to_entities" {:post transform/resource->entities}]
    ["/entities_to_resource" {:post transform/entities->resource}]]

   [["/schemas" {:get (fn [req]
                        (ok (schemas/get-all req)))}]
    ["/schemas/:id" {:get  (fn [req]
                             (ok (schemas/get-schema req)))
                     :post (fn [req]
                             (ok (schemas/save-schema req)))
                     :delete (fn [req]
                               (ok (schemas/remove-schema req)))}]]

   ["/scim"
    ["/v2"
     ["/users/:id" {:get    users/get-user
                    :patch  users/update-user
                    :delete users/delete-user}]
     ["/users" {:get  users/get-users
                :post users/create-user}]]]

   ["/ping"
    {:get ping-handler}]

   ["/math"
    {:swagger {:tags ["math"]}}

    ["/plus"
     {:get  {:summary    "plus with spec query parameters"
             :parameters {:query {:x int?, :y int?}}
             :responses  {200 {:body {:total pos-int?}}}
             :handler    (fn [{{{:keys [x y]} :query} :parameters}]
                           {:status 200
                            :body   {:total (+ x y)}})}
      :post {:summary    "plus with spec body parameters"
             :parameters {:body {:x int?, :y int?}}
             :responses  {200 {:body {:total pos-int?}}}
             :handler    (fn [{{{:keys [x y]} :body} :parameters}]
                           {:status 200
                            :body   {:total (+ x y)}})}}]]

   ["/files"
    {:swagger {:tags ["files"]}}

    ["/upload"
     {:post {:summary    "upload a file"
             :parameters {:multipart {:file multipart/temp-file-part}}
             :responses  {200 {:body {:name string?, :size int?}}}
             :handler    (fn [{{{:keys [file]} :multipart} :parameters}]
                           {:status 200
                            :body   {:name (:filename file)
                                     :size (:size file)}})}}]

    ["/download"
     {:get {:summary "downloads a file"
            :swagger {:produces ["image/png"]}
            :handler (fn [_]
                       {:status  200
                        :headers {"Content-Type" "image/png"}
                        :body    (-> "public/img/warning_clojure.png"
                                     (io/resource)
                                     (io/input-stream))})}}]]])

