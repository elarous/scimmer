(ns scimmer.mapping.subs
  (:require
   [re-frame.core :as rf]
   [scimmer.services.mapping :refer [build-resource]]
   [scimmer.mapping.utils :refer [assoc-id single-attr->schema object-attr->schema array-attr->schema extension->schema]]))

(defn- extract-attrs [ext]
  (let [ext-attrs
        (->> (:attrs ext)
             (map assoc-id)
             (map (fn [attr]
                    (case (:type attr)
                      :single attr
                      :object (update attr :sub-attrs (partial map assoc-id))
                      :array (update attr :sub-items (partial map assoc-id))
                      (throw (js/Error. "Shouldn't fall here"))))))]
    (group-by :type ext-attrs)))

;; Schema subscriptions


(rf/reg-sub
 :mapping/schema
 (fn [db _]
   (:schema db)))

;; Resource subscriptions
(rf/reg-sub
 :mapping/resource
 (fn [db _]
   (:resource db)))

(rf/reg-sub
 :mapping/resource-json
 :<- [:mapping/resource]
 (fn [resource]
   (clj->js resource)))

(rf/reg-sub
 :mapping/resource-json-str
 :<- [:mapping/resource-json]
 (fn [json]
   (.stringify js/JSON json nil 2)))

;; Entities subscriptions
(rf/reg-sub
 :mapping/entities
 :<- [:mapping/resource]
 :<- [:mapping/single-attrs]
 :<- [:mapping/object-attrs]
 :<- [:mapping/array-attrs]
 :<- [:mapping/extensions]
 (fn [[resource singles objects arrays exts] _]
   (let [single-attrs (map single-attr->schema singles)
         object-attrs (map object-attr->schema objects)
         array-attrs (map array-attr->schema arrays)
         extensions (mapv (fn [ext]
                            (let [{:keys [single object array]} (extract-attrs ext)]
                              (extension->schema ext
                                                 (map single-attr->schema single)
                                                 (map object-attr->schema object)
                                                 (map array-attr->schema array)))) exts)
         schema (hash-map :type :map
                          :children (vec (concat single-attrs object-attrs array-attrs extensions)))]
     (try
       (build-resource schema resource [] {} {})
       (catch js/Error e (js/console.error e))))))

(rf/reg-sub
 :mapping/entities-json
 :<- [:mapping/entities]
 (fn [entities _]
   (.stringify js/JSON (clj->js entities) nil 2)))


