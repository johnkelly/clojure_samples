(ns sicp.chapter_one)

;1.2
; (/ (+ 5 4 (- 2 (- 3 (+ 6 (/ 4 5))))) (* 3 (- 6 2) (- 2 7)))

;1.3
(defn square [x]
  (* x x))

(defn three_args [a b c]
  (let [args        (list a b c)
        two-largest (rest (sort args))]
    (reduce + (map square two-largest))))

;1.4
(defn a-plus-abs-b [a b]
  ((if (> b 0)
    +
    -)
   a b))


;1.5
; (defn p [_] (p))

; (defn one-five-test [x y]
;   (if (= x 0)
;     0
;     y))
; (one-five-test 0 (p))
; Applicative order will evalutate the operands immediatley resulting in p in an infinite loop.
; (one-five-test 0 p) evaluates p which calls itself

;Normal Order will not evaluate the expressions until an expression with primitives is produced in which case p will never be called:
; (= 0 0) 0 p

;1.6
; When Alyssa uses new if to compute the square root, all the operands are evaluated immediatly which results in sqrt-iter becoming stuck in an infinite loop by the evaluation.

;1.7

(defn abs [x]
  (if (> x 0)
    x
    (* -1 x)))

(defn average [x y]
  (/ (+ x y) 2))

; (time (average 4 5))

(defn improve [guess x]
  (average guess (/ x guess)))

(defn good-enough? [guess last-guess x]
  (< (abs (/ (- guess last-guess) guess)) 0.00000001))

(defn sqrt-iter [guess last-guess x]
  (if (good-enough? guess last-guess x)
    guess
    (sqrt-iter (improve guess x) guess x)))

;1.8 Cube Root

(defn improve-cube [guess x]
  (/ (+ (/ x (* guess guess)) (* 2 guess)) 3))

(defn cube-root [guess last-guess x]
  (if (good-enough? guess last-guess x)
    (double guess)
    (cube-root (improve-cube guess x) guess x)))


(defn recur-factorial [n]
  (if (= n 1)
    n
    (* n (recur-factorial (dec n)))))

(defn iter-factorial [n]
  (loop [current 1
         acc     1]
        (if (< n current)
          acc
          (recur (inc current) (* acc current)))))

(defn adding [a b]
  (if (= a 0)
    b
    (inc (adding (dec a) b))))

;Linear Recursion
; (adding 4 5)
; (inc (adding 3 5))
; (inc (inc (adding 2 5)))
; (inc (inc (inc (adding 1 5))))
; (inc (inc (inc (inc (adding 0 5)))))
; (inc (inc (inc (inc 5))))
; (inc (inc (inc 6)))
; (inc (inc 7))
; (inc 8)
; 9

(defn adding2 [a b]
  (if (= a 0)
    b
    (adding2 (dec a) (inc b))))

; Linear Iteration
; (adding2 5 4)
; (adding2 4 5)
; (adding2 3 6)
; (adding2 2 7)
; (adding2 1 8)
; (adding2 0 9)

(defn ackermann [x y]
  (cond
    (= y 0) 0
    (= x 0) (* 2 y)
    (= y 1) 2
    :else
    (ackermann (- x 1) (ackermann x (- y 1)))))

; (ackermann 1 10)
; (ackermann 2 4)
; (ackermann 3 3)


(defn fib-tree [n]
  (cond
    (= n 0) 0
    (= n 1) 1
    :else
    (+ (fib-tree (- n 1)) (fib-tree (- n 2)))))


(time (fib-tree 10))
(time (fib-tree 40))

; a := a + b
; b := a
(defn fib-iter [n]
  (loop [counter n
         a       0
         b       1]
        (if (= counter 0)
          a
          (recur (dec counter) b (+ a b)))))

; (time (fib-iter 10))
; (time (fib-iter 40))

; (time (fib-iter 25))
; (time (fib-tree 25))
