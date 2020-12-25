(ns scimmer.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [scimmer.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[scimmer started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[scimmer has shut down successfully]=-"))
   :middleware wrap-dev})
