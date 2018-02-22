(ns nmf.nmf
  (:use [clojure.math numeric-tower]))

(defn nested-map
  [f a b]
  (map (fn [x] (map (fn [y] (f x y)) b)) a))

(defn matr-map
  [f & matrs]
  (apply map (fn [& rows] (apply map (fn [& elems] (apply f elems)) rows)) matrs))

(defn matr-doall
  [A]
  (doall (map (fn [a] (doall a)) A)))

(defn height
  "Returns the height of the given matrix"
  [A]
  (count A))

(defn width
  "Returns the width of the given matrix"
  [A]
  (count (first A)))

(defn rand-matr
  "Return a matrix of the given size, consisting of random elements
  between 0 (inclusive) and n (exclusive)"
  [h w n]
  (nested-map (fn [x y] (rand n)) (range h) (range w)))

(defn transpose
  "Given a matrix, return the transpose of that matrix"
  [A]
  (apply map vector A))

(defn matr-mult
  "Given two matrices, return the result of matrix multiplication"
  ([A B]
    (nested-map (fn [x y] (reduce + (map * x y))) A (transpose B)))
  ([A B & more]
    (reduce matr-mult (matr-mult A B) more)))

(defn diff-cost
  [A B]
  (reduce + (flatten (matr-map (fn [x y] (expt (- x y) 2)) A B))))

(defn nmf
  "Given non-negative matrix V, returns two matrices W and H such that V = WH
  Implemented using multiplicative update rule (Lee and Seung, 2001)"
  [V k max-iter]
  (loop [W (rand-matr (height V) k 1)
         H (rand-matr k (width V) 1)
         iter max-iter]
    (println iter " " (diff-cost V (matr-mult W H))) ; DEBUG
    (if (or (<= iter 0) (= (diff-cost V (matr-mult W H)) 0))
      [W H]
      (let [new-H (matr-doall (matr-map * H
                    (matr-map / (matr-mult (transpose W) V)
                                (matr-mult (transpose W) W H))))]
        (recur
          (matr-doall (matr-map * W
            (matr-map / (matr-mult V (transpose new-H))
                        (matr-mult W new-H (transpose new-H)))))
          new-H
          (- iter 1))))))

; (defn nmf
;   "Given non-negative matrix V, returns two matrices W and H such that V = WH
;   Implemented using multiplicative update rule (Lee and Seung, 2001)"
;   [V k max-iter]
;   (loop [W (rand-matr (height V) k 1)
;          H (rand-matr k (width V) 1)
;          iter max-iter]
;     (println iter " " (diff-cost V (matr-mult W H))) ; DEBUG
;     (if (or (<= iter 0) (= (diff-cost V (matr-mult W H)) 0))
;       [W H]
;       (recur
;         (matr-doall (matr-map * W
;           (matr-map / (matr-mult V (transpose H))
;                       (matr-mult W H (transpose H)))))
;         (matr-doall (matr-map * H
;           (matr-map / (matr-mult (transpose W) V)
;                       (matr-mult (transpose W) W H))))
;         (- iter 1)))))
