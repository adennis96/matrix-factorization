(ns nmf.core
  (:use [nmf nmf] ; matrix]
        [uncomplicate.neanderthal core native]
        [uncomplicate.fluokitten core]
        [clojure.math numeric-tower])
  (:gen-class))

(def X
  (dge 4 5 (flatten [[2 0 1 2 0] [0 2 1 0 1] [1 0 0 2 2] [2 1 0 0 1]]) {:layout :row}))

(defn -main
  []
  (let [[W H] (nmf X 4 5000)]
    (println "X: " X)
    (println "W: " W)
    (println "H: " H)
    (println "WH: " (mm W H))
    (println "WH(rounded): " (fmap (fn ^double [^double x] (round x)) (mm W H)))))
