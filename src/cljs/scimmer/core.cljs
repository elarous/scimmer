(ns scimmer.core
  (:require
    [day8.re-frame.http-fx]
    [reagent.dom :as rdom]
    [reagent.core :as r]
    [re-frame.core :as rf]
    [goog.events :as events]
    [goog.history.EventType :as HistoryEventType]
    [markdown.core :refer [md->html]]
    [scimmer.ajax :as ajax]
    [scimmer.events]
    [reitit.core :as reitit]
    [reitit.frontend.easy :as rfe]
    ["grommet" :refer [Grommet Button]]
    [clojure.string :as string]
    [scimmer.mapping.views :refer [mapping-page]]
    [herb.core :refer [<class]]
    [scimmer.styles :as stl]
    [scimmer.nav.views :refer [navbar]])
  (:import goog.History))

(defn about-page []
  [:section.section>div.container>div.content
   [:img {:src "/img/warning_clojure.png"}]])

(defn home-page []
  [mapping-page])

(defn page []
  (if-let [page @(rf/subscribe [:common/page])]
    [:div {:class (<class stl/page-style)}
     [navbar]
     [page]]))

(defn navigate! [match _]
  (rf/dispatch [:common/navigate match]))

(def router
  (reitit/router
    [["/" {:name :mapping
           :view #'home-page
           #_:controllers #_[{:start (fn [_] (rf/dispatch [:page/init-home]))}]}]
     ["/about" {:name :about
                :view #'about-page}]]))

(defn start-router! []
  (rfe/start!
    router
    navigate!
    {}))

;; -------------------------
;; Initialize app
(defn ^:dev/after-load mount-components []
  (rf/clear-subscription-cache!)
  (rdom/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (rf/dispatch-sync [:initialize-db])
  (start-router!)
  (ajax/load-interceptors!)
  (mount-components))
