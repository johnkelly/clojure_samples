(ns fibonacci.core-test
  (:require [clojure.test :refer :all]
            [fibonacci.core :refer :all]))

(deftest iterative-test
  (testing "Happy Path test cases for the iterative fibonacci algorithm"
    (is (= 0 (iterative 0)))
    (is (= 1 (iterative 1)))
    (is (= 55 (iterative 10)))))


(deftest recursive-test
  (testing "Happy Path test cases for the iterative fibonacci algorithm"
    (is (= 0 (iterative 0)))
    (is (= 1 (iterative 1)))
    (is (= 55 (iterative 10)))))
