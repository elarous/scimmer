(ns scimmer.mapping.utils)

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
