(ns scimmer.mapping.schema-card.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 :mapping/schema
 (fn [db _]
   (:schema db)))

(rf/reg-sub
 :mapping/schemas
 (fn [db _]
   (:schemas db)))
