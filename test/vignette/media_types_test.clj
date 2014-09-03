(ns vignette.media-types-test
  (:require [vignette.media-types :refer :all]
            [midje.sweet :refer :all]))

(def archive-map {:wikia "bucket"
                  :top-dir "a"
                  :middle-dir "ab"
                  :original "boat.jpg"
                  :revision "12345"
                  :thumbnail-mode "thumbnail"
                  :width "200"
                  :height "300"
                  :options {}})

(def latest-map (assoc archive-map :revision "latest"))

(def filled-map (assoc latest-map :options {:fill "green"}))

(facts :revision
       (revision archive-map) => "12345"
       (revision latest-map) => nil)

(facts :revision-filename
       (revision-filename archive-map) => "12345!boat.jpg"
       (revision-filename latest-map) => "boat.jpg")

(facts :original-path
       (original-path archive-map) => "archive/a/ab/12345!boat.jpg"
       (original-path latest-map) => "a/ab/boat.jpg")

(facts :thumbnail-path
       (thumbnail-path archive-map) => "archive/a/ab/12345!boat.jpg/200px-300px-thumbnail-boat.jpg"
       (thumbnail-path latest-map) => "a/ab/boat.jpg/200px-300px-thumbnail-boat.jpg")

(facts :thumbnail-path-filled
       (thumbnail-path filled-map) => "a/ab/boat.jpg/200px-300px-thumbnail[fill=green]-boat.jpg")
