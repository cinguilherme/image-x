(ns image-x.file-resolver
  (:require [cheshire.core :as cs]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [clojure.edn :as edn]))

(defn- tap [v] (do (println v) v))

(defn- path->string [path]
  (-> path
      io/file
      slurp))

(defn from-json->list-uris
  "Convert a json file to a list of uris."
  [path]
  (-> path
      path->string
      cs/parse-string
      clojure.walk/keywordize-keys
      :uris))

(defn from-edn->list-uris
  "Convert a edn file to a list of uris."
  [path]
  (-> path
      path->string
      edn/read-string
      :uris))

(defn from-csv->list-uris
  "Convert a csv file to a list of uris. each line on file must be an uri."
  [path]
  (-> path
      io/reader
      csv/read-csv
      vec
      flatten
      vec))

(comment

  (from-csv->list-uris "resources/images.csv")
  (from-edn->list-uris "resources/images.edn")
  (from-json->list-uris "resources/images.json")

  (-> "resources/images.json"
      io/file
      slurp
      cs/parse-string
      clojure.walk/keywordize-keys
      :uris))


