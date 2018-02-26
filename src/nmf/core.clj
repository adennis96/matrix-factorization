(ns nmf.core
  (:use [nmf nmf matrix]
        [clojure.math numeric-tower])
  (:gen-class))

(def X
  [[2 0 1 2 0] [0 2 1 0 1] [1 0 0 2 2] [2 1 0 0 1]])

(defn -main
  []
  (let [[W H] (nmf X 4 5000)]
    (println "X: " X)
    (println "W: " W)
    (println "H: " H)
    (println "WH: " (matr-mult W H))
    (println "WH(rounded): " (matr-map round (matr-mult W H)))))
