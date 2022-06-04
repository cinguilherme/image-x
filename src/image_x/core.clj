(ns image-x.core
  "Core namespace with main functionality
  for accepting images URI's
  and producing single zip file with results"
  (:require [image-x.file-resolver :as f]
            [image-x.image-source :as source]))


(defn edn-path->zip-images!
  "Path to edn file with format like
  {:uris [\"https://somewhere.image.png\"]}"
  [path]
  (-> path
      f/from-edn->list-uris
      source/images->zip))

(defn json-path->zip-images!
  "Path to edn file with format like
  {uris: [\"https://somewhere.image.png\"]}"
  [path]
  (-> path
      f/from-json->list-uris
      source/images->zip))

(defn csv-path->zip-images!
  "Path to csv file with single header uri
  and each line as the uri to the image format like
  header uri
  http://image.format"
  [path]
  (-> path
      f/from-csv->list-uris
      source/images->zip))

(defn uri-list->zip-images
  "sequence of strings representing the uris for
  the images produce zip file with all images
  and its resized counterparts"
  [coll]
  (-> coll
      source/images->zip))


(defn image-uri->zip-images
  "string representing the uri for
  the image produce zip file with image
  and its resized counterparts"
  [uri]
  (-> uri
      vec
      source/images->zip))

(comment
  (csv-path->zip-images! "resources/images.csv")
  (time (json-path->zip-images! "resources/images.json"))
  (edn-path->zip-images! "resources/images.edn"))




