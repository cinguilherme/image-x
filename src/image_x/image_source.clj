(ns image-x.image-source
  (:require [clojure.java.io :as io]
            [clojure.core.async :as async]
            [clj-file-zip.core :as zip]))

(defn uri->image-name
  "Convert a URI to an image name."
  [uri]
  (last (clojure.string/split uri #"/")))

(defn uri->img-map [uri]
  {:uri uri
   :name (uri->image-name uri)})

(defn uri-map->image!
  "Convert a URI to an image."
  [uri-map]
  (let [in (io/input-stream (:uri uri-map))
        name (str "temp-source-" (:name uri-map))
        out (io/output-stream (io/file name))]
    (io/copy in out)
    "temp-source-image.png"))

(defn zip-images [images]
  "Zip images into a single image."
  (zip/zip-files images "zip-images.zip"))

(defn clear-temps []
  "Clear temporary files."
  (io/delete-file "temp-source-image.png"))

(defn images->zip [images-uris]
  "takes sequence of image URI and make a zip file with the downloaded images."
  (->> images-uris
       (map uri->image!)
       zip-images))


;; topology of core.async pipeline for download image, resizes of images, and zip them

(comment

  (def cloj-image-uri "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5d/Clojure_logo.svg/1024px-Clojure_logo.svg.png")

  (last (clojure.string/split cloj-image-uri #"/"))

  (uri->image-name cloj-image-uri)


  (images->zip [cloj-image-uri])
  (clear-temps)




  (zip-images [(uri->image! cloj-image-uri)])

  )
