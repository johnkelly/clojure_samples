(ns sicp.chapter-one-test
    (:use midje.sweet)
    (:require [clojure.test     :refer :all]
              [sicp.chapter_one :refer :all]))

(facts "Chapter 1 Examples"
  (fact "Three args sums the square of the two largest args"
    (three_args 1 2 3)   => 13
    (three_args 1 20 30) => 1300)

  (fact "Add the absolute value of b to a"
    (a-plus-abs-b 10 100)  => 110
    (a-plus-abs-b 10 -100) => 110)

  (fact "Average"
    (average 10 20)    => 15
    (average 1.0 2.0)  => 1.5)

  (fact "improve"
    (improve 1 2)    => (/ 3 2)
    (improve 1.5 2)  => (/ (+ (/ 2 1.5) 1.5) 2))

  (fact "Cube Root"
    (cube-root 1 0 8) => 2.0))

(facts "Book Examples"
  (fact "factorials"
    (recur-factorial 6) => 720
    (iter-factorial  6) => 720)

  (fact "fibonacci"
    (fib-tree 5) => 5
    (fib-tree 7) => 13

    (fib-iter 5) => 5
    (fib-iter 7) => 13))
