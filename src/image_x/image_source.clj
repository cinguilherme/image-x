(ns image-x.image-source
  (:require [clojure.java.io :as io]
            [clj-file-zip.core :as zip]))

(defn uri->image!
  "Convert a URI to an image."
  [uri]
  (let [in (io/input-stream uri)
        out (io/output-stream (io/file "temp-source-image.png"))]
    (io/copy in out)
    "temp-source-image.png"))

;; support uri to png or svn but also base64 links

(defn zip-images [images]
  "Zip images into a single image."
  (zip/zip-files images "zip-images.zip"))


(comment

  (def cloj-image-uri "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5d/Clojure_logo.svg/1024px-Clojure_logo.svg.png")

  (->> [cloj-image-uri]
      (map uri->image!)
       zip-images)

  (zip-images [(uri->image! cloj-image-uri)])

  )
