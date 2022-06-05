(ns image-x.image-source
  (:require [clojure.java.io :as io]
            [clj-file-zip.core :as zip]
            [clojure.edn :as edn]
            [image-resizer.format :as format]
            [image-resizer.resize :as resize]
            [image-resizer.scale-methods :as scales]))

(defn- tap [v] (do (println v) v))

(defn load-image-uris-from-resource
  ([]
   (-> "resources/images.edn" slurp edn/read-string :uris))
  ([file-path]
   (-> file-path slurp edn/read-string :uris)))

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

(defn resize-with-new-names-2 [fnz image-name]
  (let [new-name (str (:size fnz) "_" image-name)
        f (:fn fnz)]
    (-> (io/file image-name)
        f
        (format/as-file new-name :verbatim))
    (println new-name)
    new-name))

(defn make-resize-fn-coll
  [sizes-list]
  (mapv (fn [{:keys [height width]}]
          {:fn   (resize/resize-fn height width scales/ultra-quality)
           :size height})
        sizes-list))

(def default-sizes-fn
  (make-resize-fn-coll
    [{:height 500 :width 500}
     {:height 200 :width 200}
     {:height 100 :width 100}]))

(defn image-names-fnz->comb [image-names fnz]
  (mapv
    (fn [name] {:image name :fnz fnz})
    image-names))

(defn explode [fnz image-names]
  (let [comb (tap (image-names-fnz->comb image-names fnz))]
    (mapv (fn [{:keys [image fnz]}]
           (->> fnz
                (pmap (fn [f] (resize-with-new-names-2 f image)))))
          comb)))

(defn resize-images!->collect-names
  ([images-names]
   (let [_ (println "Resizing images...")
         _ (println images-names)
         rxp (explode default-sizes-fn images-names)
         _ (println rxp)
         _ (println "Resizing images...done")]
     (->> [rxp images-names] flatten vec))))

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


