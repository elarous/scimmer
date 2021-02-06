(ns scimmer.mapping.utils)

(defn assoc-id
  "To be used with a map func over a hash-map to assoc the key as an :id
  ex: {1 {:name \"first\"} 2 {:name \"second\"}}
  becomes: [{:id 1 :name \"first\"} {:id 2 :name \"second\"}] "
  [[k v]]
  (assoc v :id k))

(defn single-attr->schema [{:keys [name mapped-to group]}]
  (vector (keyword name)
          {:scimmer.services.schema/mapping (keyword group mapped-to)}
          {:type string?}))

(defn object-attr->schema [{:keys [name sub-attrs]}]
  [(keyword name)
   nil
   {:type :map
    :children
          (mapv (fn [{:keys [name mapped-to group]}]
                  [(keyword name)
                   {:scimmer.services.schema/mapping (keyword group mapped-to)}
                   {:type string?}]) sub-attrs)}])

(defn array-attr->schema [{:keys [name sub-items]}]
  [(keyword name)
   nil
   {:type :vector
    :children
          [{:type       :multi
            :properties {:dispatch :type}
            :children   (mapv (fn [{:keys [mapped-to type group]}]
                                [(keyword type)
                                 nil
                                 {:type     :map
                                  :children [[:type nil {:type := :children [(keyword type)]}]
                                             [:value {:scimmer.services.schema/mapping (keyword group mapped-to)} {:type string?}]]}])
                              sub-items)}]}])

(defn extension->schema [{:keys [label attrs]}]
  [(keyword (str "urn:ietf:params:scim:schemas:extension:" (clojure.string/lower-case label) ":2.0:User"))
   nil
   {:type :map
    :children
          (->> (vals attrs)
               (mapv (fn [attr]
                       (case (:type attr)
                         :single (single-attr->schema attr)
                         :object (object-attr->schema (update attr :sub-attrs (partial map assoc-id)))
                         :array (array-attr->schema (update attr :sub-items (partial map assoc-id)))
                         (js/console.error "Can't transform attr to schema" attr)))))}])

