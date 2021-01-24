(ns scimmer.nav.views
  (:require
    [re-frame.core :as rf]
    [herb.core :refer [<class]]
    [scimmer.nav.styles :as stl]))

;; SVG Icons
(defn mapping-icon [active?]
  [:svg {:class (<class stl/icon-style) :id "compare_arrows", :xmlns "http://www.w3.org/2000/svg", :xmlns:xlink "http://www.w3.org/1999/xlink", :width "28", :height "28", :viewbox "0 0 28 28"}
   [:defs
    [:clippath {:id "clip-path"}
     [:path {:id "Path_2", :data-name "Path 2", :d "M0,0H28V28H0Z", :fill "#cbcbcb"}]]]
   [:g {:id "compare_arrows-2", :data-name "compare_arrows", :clip-path "url(#clip-path)"}
    [:path {:class (<class stl/icon-path-style active?) :id "arrows", :data-name "Path 1", :d "M10.178,15.5H2v2.333h8.178v3.5l4.655-4.667L10.178,12Zm6.977-1.167v-3.5h8.178V8.5H17.155V5L12.5,9.667Z", :transform "translate(0.333 0.833)", :fill "#cbcbcb"}]] "&lt;"])

(defn about-icon [active?]
  [:svg {:class (<class stl/icon-style) :xmlns "http://www.w3.org/2000/svg", :width "28", :height "28", :viewbox "0 0 28 28"}
   [:g {:id "info-outline", :transform "translate(12 12)"}
    [:g {:id "info-outline-2", :data-name "info-outline", :transform "translate(-12 -12)"}
     [:g {:id "info"}
      [:rect {:id "Rectangle_10", :data-name "Rectangle 10", :width "28", :height "28", :transform "translate(28 28) rotate(180)", :fill "#cbcbcb", :opacity "0"}]
      [:path {:class (<class stl/icon-path-style active?) :id "Path_9", :data-name "Path 9", :d "M13.667,2A11.667,11.667,0,1,0,25.333,13.667,11.667,11.667,0,0,0,13.667,2Zm0,21A9.333,9.333,0,1,1,23,13.667,9.333,9.333,0,0,1,13.667,23Z", :transform "translate(0.333 0.333)", :fill "#cbcbcb"}]
      [:circle {:class (<class stl/icon-path-style active?) :id "Ellipse_1", :data-name "Ellipse 1", :cx "1.167", :cy "1.167", :r "1.167", :transform "translate(12.833 8.167)", :fill "#cbcbcb"}]
      [:path {:class (<class stl/icon-path-style active?) :id "Path_10", :data-name "Path 10", :d "M12.167,10A1.167,1.167,0,0,0,11,11.167V17a1.167,1.167,0,1,0,2.333,0V11.167A1.167,1.167,0,0,0,12.167,10Z", :transform "translate(1.833 1.667)", :fill "#cbcbcb"}]]]]])

(defn dashboard-icon [active?]
  [:svg {:class (<class stl/icon-style) :id "pie-chart-outline", :xmlns "http://www.w3.org/2000/svg", :width "28", :height "28", :viewbox "0 0 28 28"}
   [:g {:id "pie-chart-outline-2", :data-name "pie-chart-outline", :transform "translate(0 0)"}
    [:g {:id "pie-chart"}
     [:rect {:id "Rectangle_7", :data-name "Rectangle 7", :width "28", :height "28", :fill "#00b7d5", :opacity "0"}]
     [:path {:class (<class stl/icon-path-style active?) :id "Path_5", :data-name "Path 5", :d "M13.167,2A1.167,1.167,0,0,0,12,3.167V12.5a1.167,1.167,0,0,0,1.167,1.167H22.5A1.167,1.167,0,0,0,23.667,12.5,10.5,10.5,0,0,0,13.167,2Zm1.167,9.333V4.415a8.167,8.167,0,0,1,6.918,6.918Z", :transform "translate(2 0.333)", :fill "#00b7d5"}]
     [:path {:class (<class stl/icon-path-style active?) :id "Path_6", :data-name "Path 6", :d "M23.957,15.984a1.167,1.167,0,0,0-1.493.712A9.333,9.333,0,1,1,10.552,4.784a1.168,1.168,0,1,0-.77-2.205A11.667,11.667,0,1,0,24.668,17.466,1.167,1.167,0,0,0,23.957,15.984Z", :transform "translate(0.333 0.419)", :fill "#00b7d5"}]]]])
;;

(defn nav-link [uri icon title page]
  (let [active? (= page @(rf/subscribe [:common/page-id]))]
    [:a {:class (<class stl/link-style)
         :href  uri}
     [:div {:class (<class stl/active-link-indicator active?)}]
     [:div
      [icon active?]]
     [:div {:class (<class stl/link-title-style active?)} title]]))

(defn navbar []
  [:nav {:class (<class stl/nav-style)}
   [:div
    [:div {:class (<class stl/nav-logo-style)} "Scimmer"]
    [:div {:class (<class stl/nav-main-links)}
     [nav-link "#/" mapping-icon "Mapping" :mapping]]]
   [:div {:class (<class stl/footer-style)}
    [nav-link "#/about" about-icon "About" :about]]])
