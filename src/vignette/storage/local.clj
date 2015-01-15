(ns vignette.storage.local
  (:require [clojure.java.io :as io]
            [clojure.java.shell :as sh]
            [pantomime.mime :refer [mime-type-of]]
            [vignette.media-types :as mt]
            [vignette.storage.protocols :refer :all]
            [vignette.util.filesystem :refer :all])
  (:import  (java.io FileInputStream)))

(declare create-stored-object)

; TODO: when the logger has a closure port, we should create an exception that has context that we can log w/ exceptions
(defrecord LocalStorageSystem [directory]
  StorageSystemProtocol

  (get-object [this bucket path]
    (let [real-file (io/file (resolve-local-path (:directory this) bucket path))]
      (when (file-exists? real-file)
        (create-stored-object real-file))))

  (put-object [this resource bucket path]
    (let [real-path (resolve-local-path (:directory this) bucket path)]
      (create-local-path (get-parent real-path))
      (if (transfer! resource real-path)
        true
        (throw (Exception. "put-object failed")))))

  (delete-object [this bucket path]
    (let [real-path (resolve-local-path (:directory this) bucket path)]
     (let [status (io/delete-file real-path :silently true)]
       (if (= status :silently)
         false
         true))))

  (list-buckets [this])
  (list-objects [this bucket]))

(defrecord LocalStoredObject [file stream-close-callbacks]
  StoredObjectProtocol
  (file-stream [this]
    (:file this))
  (content-length [this]
    (file-length (file-stream this)))
  (content-type [this]
    (mime-type-of (file-stream this)))
  (->response-object [this]
    (let [file (file-stream this)
          stored-object this]
      (proxy [FileInputStream] [file]
        (close []
          (proxy-super close)
          (doall (map #(% stored-object) (:stream-close-callbacks stored-object)))))))
  (transfer! [this to]
    (io/copy (file-stream this)
             (io/file to))
    (file-exists? to)))

(defn create-local-storage-system
  [directory]
  (->LocalStorageSystem directory))

(defn create-stored-object
  [file & callbacks]
  (->LocalStoredObject file callbacks))
