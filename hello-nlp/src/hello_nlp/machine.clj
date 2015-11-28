(ns hello-nlp.machine
  (:refer-clojure :exclude [* - + == /])
  (:require [core.matrix :refer :all]
            [core.matrix.operators :refer :all]))

;;Neurons
;;  Input Hidden  Output
;;  A     1       C
;;  B     2       D
;;        3


;; Connection Strengths
;; Input to Hidden => [[A1 A2 A3] [B1 B2 B3]]
;; Hidden to Output => [[1C 1D] [2C 2D] [3C 3D]]


(def input-neurons [1 0])
(def input-hidden-strengths [ [0.12 0.2 0.13]
                              [0.01 0.02 0.03]])
(def hidden-neurons [0 0 0])
(def hidden-output-strengths [[0.15 0.16]
                              [0.02 0.03]
                              [0.01 0.02]])


(def activation-fn (fn [x] (Math/tanh x)))
(def dactivation-fn (fn [y] (- 1.0 (* y y))))

(defn layer-activation [inputs strengths]
  "forward propagate the input of a layer"
  (mapv activation-fn
      (mapv #(reduce + %)
       (* inputs (transpose strengths)))))

(layer-activation input-neurons input-hidden-strengths)


(+ [[1 2]
    [3 4]]
   (* (identity-matrix 2) 3.0))

(def a (matrix [[2 0] [0 2]]))

a

 (* a [[1 2] [3 4]])

(transpose [[1 2] [3 5]])

