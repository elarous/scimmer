(ns scimmer.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[scimmer started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[scimmer has shut down successfully]=-"))
   :middleware identity})
