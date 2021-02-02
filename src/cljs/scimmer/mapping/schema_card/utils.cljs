(ns scimmer.mapping.schema-card.utils)

;; helper functions
(defn get-entity-mapping [ns-k]
  (let [mapping (namespace ns-k)
        entity (name ns-k)]
    {:mapping mapping :entity entity}))

(defn get-mapping-attr-item [schema]
  (->> schema
       :children
       (some (fn [v] (and (= (first v) :value) v)))
       second
       :scimmer.services.schema/mapping))