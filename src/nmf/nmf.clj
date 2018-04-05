(ns nmf.nmf
  (:use ; [nmf matrix]
        [uncomplicate.neanderthal core native]
        [uncomplicate.fluokitten core]
        [clojure.math numeric-tower]))

(defn rand-matr
  [h w n]
  (dge h w (for [x (range h) y (range w)] (rand n))))

(defn diff-cost
  [A B]
  (fold (fmap (fn ^double [^double x ^double y] (expt (- x y) 2)) A B)))

(defn nmf
  "Given non-negative matrix V, returns two matrices W and H such that V = WH
  Implemented using multiplicative update rule (Lee and Seung, 2001)"
  [V k max-iter]
  (loop [W (rand-matr (mrows V) k 1)
         H (rand-matr k (ncols V) 1)
         iter max-iter
         start-time (System/currentTimeMillis)]
    (printf "step %2d; cost: %.4f\n" iter (diff-cost V (mm W H))) ; DEBUG
    (if (or (<= iter 0) (= (diff-cost V (mm W H)) 0))
      [W H]
      (let [new-H (fmap (fn ^double [^double x ^double y] (* x y)) H
                        (fmap (fn ^double [^double x ^double y] (/ x y))
                              (mm (trans W) V)
                              (mm (trans W) W H)))
            new-W (fmap (fn ^double [^double x ^double y] (* x y)) W
                        (fmap (fn ^double [^double x ^double y] (/ x y))
                              (mm V (trans new-H))
                              (mm W new-H (trans new-H))))]
        (printf "time: %.4f sec; " (float (/ (- (System/currentTimeMillis) start-time) 1000))) ; DEBUG
        (recur
          new-W
          new-H
          (- iter 1)
          (System/currentTimeMillis))))))
