(ns scimmer.mapping.schema-card.single-attrs-section.single-attr.events
  (:require
    [re-frame.core :as rf]
    [ajax.core :as ajax]
    [reitit.frontend.easy :as rfe]
    [reitit.frontend.controllers :as rfc]
    [scimmer.app-db :as app-db]
    [scimmer.services.mapping :refer [build-resource]]
    [scimmer.mapping.schema-card.events :refer [attr-interceptor get-attr-idx]]))

(defn single-attr-remove [[_name _props schema]]
  (contains? #{:vector :map} (:type schema)))

(rf/reg-event-db
  :mapping/>update-single-attr
  [(attr-interceptor single-attr-remove)]
  (fn [single-attrs [_ {:keys [name entity mapping]}]]
    (let [target-idx (get-attr-idx single-attrs name)]
      (assoc-in single-attrs [target-idx 1 :scimmer.services.schema/mapping]
                (keyword mapping entity)))))

(rf/reg-event-db
  :mapping/>add-single-attr
  [(attr-interceptor single-attr-remove)]
  (fn [attrs _]
    (let [default-attr
          (with-meta
            [:newAttribute {:scimmer.services.schema/mapping :user/new_attribute} {:type string?}]
            {:id (random-uuid)})]
      (conj attrs default-attr))))
