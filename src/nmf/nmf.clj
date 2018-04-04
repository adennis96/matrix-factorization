(ns nmf.nmf
  (:use [nmf matrix]
        [clojure.math numeric-tower]))

(defn diff-cost
  [A B]
  (reduce + (flatten (matr-map (fn [x y] (expt (- x y) 2)) A B))))

(defn nmf
  "Given non-negative matrix V, returns two matrices W and H such that V = WH
  Implemented using multiplicative update rule (Lee and Seung, 2001)"
  [V k max-iter]
  (loop [W (rand-matr (height V) k 1)
         H (rand-matr k (width V) 1)
         iter max-iter
         start-time (System/currentTimeMillis)]
    (printf "step %2d; cost: %.4f\n" iter (diff-cost V (matr-mult W H))) ; DEBUG
    (if (or (<= iter 0) (= (diff-cost V (matr-mult W H)) 0))
      [W H]
      (let [new-H (matr-doall (matr-map * H
                    (matr-map / (matr-mult (transpose W) V)
                                (matr-mult (transpose W) W H))))
            new-W (matr-doall (matr-map * W
                    (matr-map / (matr-mult V (transpose new-H))
                                (matr-mult W new-H (transpose new-H)))))]
        (printf "time: %.4f sec; " (float (/ (- (System/currentTimeMillis) start-time) 1000))) ; DEBUG
        (recur
          new-W
          new-H
          (- iter 1)
          (System/currentTimeMillis))))))
