(ns image-x.image-source
  (:require [clojure.java.io :as io]
            [clj-file-zip.core :as zip]
            [clojure.edn :as edn]
            [image-resizer.format :as format]
            [image-resizer.resize :as resize]
            [image-resizer.scale-methods :as scales]))

(defn load-image-uris-from-resource
  ([]
   (-> "resources/images-uris.edn" slurp edn/read-string :images-uris))
  ([file-path]
   (-> file-path slurp edn/read-string :images-uris)))

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

(defn clear-temps
  "Clear temporary files."
  [image-names]
  (mapv #(io/delete-file %) image-names))

(defn resize-with-new-names [image-name size fn]
  (let [new-name (str size "_" image-name)]
    (-> (io/file image-name)
        fn
        (format/as-file new-name :verbatim))
    new-name))

(defn resize-images!->collect-names [images-names]
  (let [fn-quality-500 (resize/resize-fn 500 500 scales/ultra-quality)
        fn-quality-200 (resize/resize-fn 200 200 scales/ultra-quality)
        fn-quality-50 (resize/resize-fn 50 50 scales/ultra-quality)
        _ (println "Resizing images... to 500x500")
        z500 (pmap #(resize-with-new-names % 500 fn-quality-500) images-names)
        _ (println "Resizing images... to 200x200")
        z200 (pmap #(resize-with-new-names % 200 fn-quality-200) images-names)
        _ (println "Resizing images... to 50x50")
        z50 (pmap #(resize-with-new-names % 50 fn-quality-50) images-names)]
    (do (println (->> [z500 z200 z50 images-names] flatten vec))
        (->> [z500 z200 z50 images-names] flatten vec))))


(defn images->zip [images-uris]
  "takes sequence of image URI and make a zip file with the downloaded images."
  (let [images (->> images-uris
                    (map uri->uri-no-params)
                    no-dups
                    (map uri->img-map)
                    (map uri-map->future-image!)
                    (map future->image!)
                    resize-images!->collect-names)
        zip-file (zip-images images)]
    (do (future (clear-temps images))
        zip-file)))


(comment

  (time (images->zip (load-image-uris-from-resource)))

  (zip-images [(uri->image! cloj-image-uri)]))


