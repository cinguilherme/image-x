(ns image-x.image-source
  (:require [clojure.java.io :as io]
            [clj-file-zip.core :as zip]))

(defn uri->uri-no-params [uri]
  (first (clojure.string/split uri #"[?]")))

(defn no-dups [uris]
  (-> uris set vec))

(defn uri->image-name
  "Convert a URI to an image name."
  [uri]
  (let [ls (last (clojure.string/split uri #"/"))
        name (first (clojure.string/split ls #"[?]"))]
    name))

(defn uri->img-map [uri]
  {:uri  uri
   :name (uri->image-name uri)})

(defn uri-map->future-image!
  "Convert a URI to an image."
  [uri-map]
  (future
    (let [in (io/input-stream (:uri uri-map))
          name (str "temp-source-" (:name uri-map))
          out (io/output-stream (io/file name))]
      (io/copy in out)
      name)))

(defn future->image!
  "Convert a future to an image."
  [future]
  @future)


(defn zip-images [images]
  "Zip images into a single image."
  (zip/zip-files images "zip-images.zip"))

(defn clear-temps [image-names]
  "Clear temporary files."
  (mapv #(io/delete-file %) image-names))

(defn images->zip [images-uris]
  "takes sequence of image URI and make a zip file with the downloaded images."
  (let [images (->> images-uris
                    (map uri->uri-no-params)
                    no-dups
                    (map uri->img-map)
                    (map uri-map->future-image!)
                    (map future->image!))
        zip-file (zip-images images)]
    (do (future (clear-temps images))
        zip-file)))


;; topology of core.async pipeline for download image, resizes of images, and zip them

(comment

  (def clj-uri "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5d/Clojure_logo.svg/1024px-Clojure_logo.svg.png")

  (def ff14 "https://images.squarespace-cdn.com/content/v1/56d5457d8259b57a20245e80/1601292966362-64C0QOLKVLD248Q5UFOD/ff14+new+player+experience.jpg?format=1500w")

  (def ff14-2 "https://sm.ign.com/ign_br/news/a/after-bein/after-being-literally-too-popular-to-handle-final-fantasy-14_auh5.jpg")

  (uri->image-name clj-uri)

  (uri->image-name ff14)

  (no-dups [clj-uri ff14 ff14-2 ff14 ff14-2 clj-uri])

  (time (images->zip [clj-uri ff14 ff14-2 ff14 ff14-2 clj-uri]))





  (zip-images [(uri->image! cloj-image-uri)]))


