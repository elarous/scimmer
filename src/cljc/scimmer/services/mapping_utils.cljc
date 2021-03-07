(ns scimmer.services.mapping-utils)

(defn assoc-id
  "To be used with a map func over a hash-map to assoc the key as an :id
  ex: {1 {:name \"first\"} 2 {:name \"second\"}}
  becomes: [{:id 1 :name \"first\"} {:id 2 :name \"second\"}] "
  [[k v]]
  (assoc v :id k))

(defn single-attr->schema [{:keys [name mapped-to collection]}]
  (vector (keyword name)
          {:scimmer.services.schema/mapping (keyword collection mapped-to)}
          {:type string?}))

(defn object-attr->schema [{:keys [name sub-attrs]}]
  [(keyword name)
   nil
   {:type :map
    :children
    (mapv (fn [{:keys [name mapped-to collection]}]
            [(keyword name)
             {:scimmer.services.schema/mapping (keyword collection mapped-to)}
             {:type string?}]) sub-attrs)}])

(defn array-attr->schema [{:keys [name sub-items]}]
  [(keyword name)
   nil
   {:type :vector
    :children
    [{:type       :multi
      :properties {:dispatch :type}
      :children   (mapv (fn [{:keys [mapped-to type collection]}]
                          [(keyword type)
                           nil
                           {:type     :map
                            :children [[:type nil {:type := :children [(keyword type)]}]
                                       [:value {:scimmer.services.schema/mapping (keyword collection mapped-to)} {:type string?}]]}])
                        sub-items)}]}])

(defn extension->schema [{:keys [name]} singles objects arrays]
  [(keyword (str "urn:ietf:params:scim:schemas:extension:" (clojure.string/lower-case name) ":2.0:User"))
   nil
   {:type :map
    :children (vec (concat singles objects arrays))}])

(defn- extract-attrs [ext]
  (group-by :type (:attrs ext)))

(defn combine-malli-schema [schema]
  (let [{:keys [single object array]} (extract-attrs schema)
        single-attrs (map single-attr->schema single)
        object-attrs (map object-attr->schema object)
        array-attrs (map array-attr->schema array)
        extensions (mapv (fn [ext]
                           (let [{:keys [single object array]} (extract-attrs ext)]
                             (extension->schema ext
                                                (map single-attr->schema single)
                                                (map object-attr->schema object)
                                                (map array-attr->schema array))))
                         (:extensions schema))
        malli-schema (hash-map :type :map
                               :children (vec (concat single-attrs object-attrs array-attrs extensions)))]
    malli-schema))

(comment
  (def schema
    {:id #uuid "0ea136ad-061d-45f1-8d92-005627a156f9"
     :resource "user"
     :name "first_schema"
     :is-default false
     :attrs [{:id #uuid "a686d33f-655f-4c0c-a228-323de2a68937"
              :name "newAttribute"
              :mapped-to "new_attribute"
              :collection "user"
              :schema-id #uuid "0ea136ad-061d-45f1-8d92-005627a156f9"
              :extension-id nil
              :type :single}]
     :extensions [{:id #uuid "961ca886-0b85-48b6-80d2-6d4f7709d7cd"
                   :name "test"
                   :schema-id #uuid "0ea136ad-061d-45f1-8d92-005627a156f9"
                   :attrs [{:id #uuid "00b0ae34-6f14-4aba-a1cb-f29fc571a663"
                            :name "newObjectAttr"
                            :schema-id nil
                            :extension-id #uuid "961ca886-0b85-48b6-80d2-6d4f7709d7cd"
                            :sub-attrs [{:id #uuid "57cba796-38b2-4728-9a26-b352f032ae53"
                                         :name "subAttr"
                                         :mapped-to "sub_attr"
                                         :collection "user"
                                         :object-attr-id #uuid "00b0ae34-6f14-4aba-a1cb-f29fc571a663"}
                                        {:id #uuid "f48344a0-f8dd-4175-a1b8-de9df56d1a43"
                                         :name "subAttr"
                                         :mapped-to "sub_attr"
                                         :collection "user"
                                         :object-attr-id #uuid "00b0ae34-6f14-4aba-a1cb-f29fc571a663"}]
                            :type :object}
                           {:id #uuid "68b0d687-0415-4cea-93c2-5c39fb6920da"
                            :name "newArrayAttr"
                            :schema-id nil
                            :extension-id #uuid "961ca886-0b85-48b6-80d2-6d4f7709d7cd"
                            :sub-items [{:id #uuid "897f4c72-5619-4be1-896c-5a0c9cc3901a"
                                         :type "type1"
                                         :mapped-to "sub_item"
                                         :collection "user"
                                         :array-attr-id #uuid "68b0d687-0415-4cea-93c2-5c39fb6920da"}
                                        {:id #uuid "c388dd21-9def-4c23-95a1-58207c4dd7de"
                                         :type "type1"
                                         :mapped-to "sub_item"
                                         :collection "user"
                                         :array-attr-id #uuid "68b0d687-0415-4cea-93c2-5c39fb6920da"}]
                            :type :array}]}]}))
