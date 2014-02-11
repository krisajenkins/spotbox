(ns spotbox.core
  (:require [clj-http.client :as client]
            [cheshire.core :refer [parse-string]]))

(defn search-spotify
  [term]
  (-> (client/get "http://ws.spotify.com/search/1/track.json"
                  {:query-params {:q term}
                   :accept :json})
      :body
      (parse-string true)))

(def songset
  (memoize
   (fn [term]
     (as-> (search-spotify term) <>
           (get-in <> [:tracks])
           (map #(select-keys % [:name :length]) <>)
           (set <>)))))

(defn value-of
  [x]
  (cond
   (map? x) (:length x)
   (set? x) (reduce + 0 (map value-of x))))

(defn longer-playlist
  [best new]
  (if (> (value-of best) (value-of new))
    best
    new))

(def knapsack
  (memoize
   (fn [songs threshold]
     {:pre [(set? songs)]}
     (reduce longer-playlist
             #{}
             (for [song songs
                   :let [list-without (knapsack (disj songs song)
                                                threshold)
                         best-plus-this (conj list-without song)]]
               (if (< (value-of best-plus-this)
                      threshold)
                 best-plus-this
                 list-without))))))

(time
 (let [length 1800
       songs (set (take 15 (songset "beer")))
       playlist (knapsack songs length)]
   {:songs songs
    :length (value-of playlist)
    :playlist playlist}))
