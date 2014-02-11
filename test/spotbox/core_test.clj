(ns spotbox.core-test
  (:require [clojure.test :refer :all]
            [spotbox.core :refer :all]))

(deftest longer-playlist-test
  (is (= #{{:length 3}
           {:length 9}})
      (longer-playlist #{}
                       #{{:length 3}
                         {:length 9}}))
  (is (= #{{:length 3}
           {:length 9}})
      (longer-playlist #{{:length 1}
                         {:length 5}}
                       #{{:length 3}
                         {:length 9}})))
