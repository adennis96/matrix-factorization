(ns nmf.matrix)

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
