(ns machine-learning.core
    (:use  [incanter.core]
          [incanter.stats]
          [incanter.charts]
          [incanter.datasets]))

(defn plot-sine
  "Creates a plot of the sin function for given x range"
  [x-min x-max]
  (view (function-plot sin x-min x-max)))

(defn plot-cosine
  "Creates a plot of the cosine function for given x range"
  [x-min x-max]
  (view (function-plot cos x-min x-max)))

(time (view (function-plot (fn [x] (+ (* 25 x x) (* 5 x) 10)) 0 10000000)))




(defn cubic [x] (+ (* x x x) (* 2 x x) (* 2 x) 3))
(view (function-plot cubic -10 10))  


(defn plot-histogram
  "Creates a histogram plot of n sample size"
  [n]
  (view (histogram (sample-normal 1000))))

; (plot-sine 0 10)
; (plot-cosine 0 10)
(time (plot-histogram 1000))

; ($= 7 + 8 - 2 * 6 / 2)

; ($= [1 2 3] + 5)

; ($= (matrix [[1 2] [4 5]] + 6))

; ($= (trans [[1 2 ] [4 5]]) + 6)

; (time ($= 8 * 3))

; (time (* 8 3))
