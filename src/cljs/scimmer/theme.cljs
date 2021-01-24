(ns scimmer.theme)

(def colors
  {:primary        "#5BC0BE"
   :highlight      "#00B7D5"
   :secondary      "#e75a7c"
   :tertiary      "#8FC47A"
   :text-primary   "#0B132B"
   :text-secondary "#1C2541"
   :bg-dark        "#595F72"
   :bg-darker      "#333A46"
   :grey-1         "#F8F9FA"
   :grey-2         "#E9ECEF"
   :grey-3         "#DEE2E6"})

(defn sizes [step] (-> step (* 8) (str "px")))

(def fonts
  {:logo    "Cookie, cursive"
   :headers "Montserrat"
   :normal  "Nunito"})

(def shadows
  {:high   "6px 2px 23px -1px rgba(0,0,0,0.51)"
   :medium "1px 2px 14px -5px rgba(0,0,0,0.42)"})

