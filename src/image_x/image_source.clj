(ns image-x.image-source
  (:require [clojure.java.io :as io]
            [clj-file-zip.core :as zip]
            [clojure.edn :as edn]
            [image-resizer.format :as format]
            [image-resizer.resize :as resize]
            [image-resizer.scale-methods :as scales]))

(defn load-image-uris-from-resource
  ([]
   (-> "resources/images.edn" slurp edn/read-string :images-uris))
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

(defn resize-with-new-names
  [image-name size fn]
  (let [new-name (str size "_" image-name)]
    (-> (io/file image-name)
        fn
        (format/as-file new-name :verbatim))
    new-name))

(defn resize-with-new-names-2 [fnz]
  (fn [image-name]
    (let [new-name (str (:size fnz) "_" image-name)]
      (-> (io/file image-name)
          (:fn fnz)
          (format/as-file new-name :verbatim))
      new-name)))

(defn make-resize-fn-coll
  [sizes-list]
  (mapv (fn [{:keys [height width] :as size-map}]
          {:fn   (resize/resize-fn height width scales/ultra-quality)
           :size height})
        sizes-list))

(def default-sizes-fn
  (make-resize-fn-coll
    [{:height 500 :width 500}
     {:height 200 :width 200}
     {:height 100 :width 100}]))

(defn xplode [fnz image-names]
  (let [colf (mapv #(resize-with-new-names-2 %) fnz)
        _ (mapv
            (fn [image-name]
              (pmap
                (fn [f] (f image-name))
                colf))
            image-names)]))

(comment
  (mapv #(resize-with-new-names-2 %) default-sizes-fn))

(defn resize-images!->collect-names
  ([images-names]
   (let [fn-quality-500 (resize/resize-fn 500 500 scales/ultra-quality)
         fn-quality-200 (resize/resize-fn 200 200 scales/ultra-quality)
         fn-quality-100 (resize/resize-fn 100 100 scales/ultra-quality)
         fn-quality-50 (resize/resize-fn 50 50 scales/ultra-quality)
         _ (println "Resizing images...")

         _ (mapv images-names)

         z500 (pmap #(resize-with-new-names % 500 fn-quality-500) images-names)
         z200 (pmap #(resize-with-new-names % 200 fn-quality-200) images-names)
         z100 (pmap #(resize-with-new-names % 100 fn-quality-100) images-names)
         z50 (pmap #(resize-with-new-names % 50 fn-quality-50) images-names)

         _ (println "Resizing images...done")]
     (->> [z500 z200 z100 z50 images-names] flatten vec))))


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


