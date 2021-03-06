(ns nmf.core
  (:use [nmf nmf matrix data]
        [clojure.math numeric-tower])
  (:gen-class))

(defn -main
  [& args]
  (when (< (count args) 3)
        (println "usage: lein run <matrix> <num-features> <max-iterations>")
        (System/exit 0))
  (let [V (eval (symbol "nmf.data" (nth args 0)))
        k (Integer/parseInt (nth args 1))
        max-iter (Integer/parseInt (nth args 2))
        [W H] (nmf V k max-iter)]
    (println "V: " V)
    ; (println "W: " W)
    ; (println "H: " H)
    ; (println "WH: " (matr-mult W H))
    (println "WH(rounded): " (matr-map round (matr-mult W H)))))
