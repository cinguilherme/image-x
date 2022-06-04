(ns image-x.core
  "Core namespace with main functionality
  for accepting images URI's
  and producing single zip file with results"
  (:require [image-x.image-source :as source]))


(defn edn-path-file->zip-images
  "Path to edn file with format like
  {:uris [\"https://somewhere.image.png\"]}"
  [path])


(defn json-path->zip-images
  "Path to edn file with format like
  {uris: [\"https://somewhere.image.png\"]}"
  [path])

(defn csv-path->zip-images!
  "Path to csv file with single header uri
  and each line as the uri to the image format like
  header uri
  http://image.format"
  [path])

(defn uri-list->zip-images
  "sequence of strings representing the uris for
  the images produce zip file with all images
  and its resized counterparts"
  [coll])


(defn image-uri->zip-images
  "string representing the uri for
  the image produce zip file with image
  and its resized counterparts"
  [uri])

(comment

  )