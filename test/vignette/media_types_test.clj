(ns vignette.media-types-test
  (:require [vignette.media-types :refer :all]
            [midje.sweet :refer :all]
            [clojure.java.io :as io])
  (:import java.lang.IllegalArgumentException
           clojure.lang.ExceptionInfo))

(facts :get-media-map
  (get-media-map {}) => (throws IllegalArgumentException)
  (get-media-map {:request-type :adjust-original}) => (throws ExceptionInfo)
  (get-media-map {:request-type :thumbnail}) => (throws ExceptionInfo)
  (get-media-map {:request-type :original}) => (throws ExceptionInfo)
  (get-media-map {:request-type "does-not-exst"}) => (throws IllegalArgumentException)

  (let [success-map {:request-type :adjust-original
                     :mode "reorient"
                     :original "ropes.jpg"
                     :top-dir "a"
                     :middle-dir "ab"
                     :wikia "bucket"}]
    (get-media-map success-map) => success-map
    (doall
      (for [filter-key (filter #(not= :request-type %) (keys success-map))]
        (get-media-map (dissoc success-map filter-key)) => (throws ExceptionInfo))))

  (let [success-map {:mode "resize"
                     :request-type :thumbnail
                     :original "ropes.jpg"
                     :top-dir "a"
                     :middle-dir "ab"
                     :wikia "bucket"
                     :height "10"
                     :width "10"}]
    (get-media-map success-map) => success-map
    (doall
      (for [filter-key (filter #(not= :request-type %) (keys success-map))]
        (get-media-map (dissoc success-map filter-key)) => (throws ExceptionInfo)))))
