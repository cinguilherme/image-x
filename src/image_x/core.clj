(ns image-x.core
  "Core namespace with main functionality
  for accepting images URI's
  and producing single zip file with results"
  (:require [image-x.file-resolver :as f]
            [image-x.image-source :as source]
            [schema.core :as s]))

(defn edn-path->zip-images!
  "Path to edn file with format like
  {:uris [\"https://somewhere.image.png\"]}"
  ([path]
   (-> path
       f/from-edn->list-uris
       source/images->zip))
  ([path config-maps]
   (->> path
        f/from-edn->list-uris
        (source/images->zip config-maps))))

(defn json-path->zip-images!
  "Path to edn file with format like
  {uris: [\"https://somewhere.image.png\"]}"
  ([path]
   (-> path
       f/from-json->list-uris
       source/images->zip))
  ([path config-maps]
   (->> path
        f/from-json->list-uris
        (source/images->zip config-maps))))

(defn csv-path->zip-images!
  "Path to csv file with single header uri
  and each line as the uri to the image format like
  header uri
  http://image.format"
  ([path]
   (-> path
       f/from-csv->list-uris
       source/images->zip))
  ([path config-maps]
   (->> path
        f/from-csv->list-uris
        (source/images->zip config-maps))))

(defn uri-list->zip-images
  "sequence of strings representing the uris for
  the images produce zip file with all images
  and its resized counterparts"
  ([coll]
   (-> coll
       source/images->zip))
  ([coll config-maps]
   (->> coll (source/images->zip config-maps))))


(defn image-uri->zip-images
  "string representing the uri for
  the image produce zip file with image
  and its resized counterparts"
  ([uri]
   (->> uri vec source/images->zip))
  ([uri config-maps]
   (->> uri vec (source/images->zip config-maps))))


(comment
  (csv-path->zip-images! "resources/images.csv")

  ;with default config
  (time (json-path->zip-images! "resources/images.json"))

  ;with external config
  (time (json-path->zip-images! "resources/images.json"
                                [{:height 400 :width 400}
                                 {:height 500 :width 500}
                                 {:height 600 :width 600}
                                 {:height 700 :width 700}]))
  (edn-path->zip-images! "resources/images.edn"))




