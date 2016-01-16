(ns fibonacci.core)

(defn iterative
  "Finds the nth number in the fibonacci series O(n)"
  [n]
  (loop [x   0
        y    1
        iter n]
        (if (< iter 1)
          x
          (recur y (+ x y) (dec iter)))))


(defn recursive
  "Finds the nth number in the fibonacci series O(2^n)"
  [n]
  (if (<= n 1)
    n
    (+ (recursive (- n 1)) (recursive (- n 2)))))

; (time (iterative 10)) "Elapsed time: 0.207049 msecs" 
; (time (recursive 10)) "Elapsed time: 0.055248 msecs" 

; (time (iterative 30)) "Elapsed time: 0.054099 msecs"  
; (time (recursive 30)) "Elapsed time: 21.788625 msecs"  

; (time (iterative 70)) "Elapsed time: 0.066598 msecs" 
; (time (recursive 70)) didn't complete
