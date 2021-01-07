(ns scimmer.theme)

(def colors
  {:primary "#5BC0BE"
   :highlight "#6FFFE9"
   :secondary "#3A506B"
   :text-primary "#0B132B"
   :text-secondary "#1C2541"
   :grey-1 "#F8F9FA"
   :grey-2 "#E9ECEF"
   :grey-3 "#DEE2E6"})

(defn sizes [step] (-> step (* 8) (str "px")))

(def fonts
  {:headers "Montserrat"
   :normal "Nunito"})
