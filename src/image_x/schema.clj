(ns image-x.schema
  (:require [schema.core :as s]))

(s/defschema FormatSizeInput
  {:height s/Num
   :width s/Num})

(s/defschema InputPayload
  [FormatSizeInput])

(def default
  [{:height 500 :width 500}
   {:height 200 :width 200}
   {:height 100 :width 100}])