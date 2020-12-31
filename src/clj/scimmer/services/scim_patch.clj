(ns scimmer.services.scim-patch
  "Functions to handle Malli schema to scim-patch transformations"
  (:require [scimmer.services.schema :as sch]))

;; helpers
(defn- symbol->keyword [symbol]
  (case symbol
    number? :number
    boolean? :boolean
    :string))
;;

(defn build-dispatch [element]
  (cond
    (and (vector? element) (first element))
    (case (:type (-> element next next first))
      :map :map
      :vector :vector
      := :=
      :attr)
    (map? element)
    (case (:type element)
      :map :map-no-name
      :multi :multi-no-name
      :attr-no-name)))

(defmulti schema->scim-patch-schema #'build-dispatch)

(defmethod schema->scim-patch-schema :attr [[name props contents]]
  (if (:children contents)
    (map schema->scim-patch-schema (:children contents))
    {:attributes {name (hash-map :type
                                 (-> (:type contents)
                                     (symbol->keyword)))}}))

(defmethod schema->scim-patch-schema :map-no-name [contents]
  (let [children-result (map schema->scim-patch-schema (:children contents))]
    (apply (partial merge-with into) children-result)))

(defmethod schema->scim-patch-schema :map [[name props contents]]
  (let [children-result (map schema->scim-patch-schema (:children contents))]
    {:attributes {name {:type (apply (partial merge-with into) children-result)}}}))

(defmethod schema->scim-patch-schema :vector [[name props contents]]
  (let [children-result (schema->scim-patch-schema (-> contents :children first))]
    {:attributes  {name children-result}}))

(defmethod schema->scim-patch-schema :multi-no-name [contents]
  (let [children-result (map schema->scim-patch-schema (:children contents))
        result (->> children-result
                    (map vals)
                    flatten
                    (apply (partial merge-with into)))]
    (assoc (-> result vals first) :multi-valued true)))

(defmethod schema->scim-patch-schema := [[name props contents]]
  {:attributes {name {:type :string}}})

(comment
   (schema->scim-patch-schema sch/user-schema-map))
