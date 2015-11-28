(ns looping-is-recursion)

(defn power [base exp]
  (let [helper (fn [acc n]
                 (if (zero? n)
                   acc
                   (recur (* acc base) (dec n))))]
    (helper 1 exp)))

(defn last-element [a-seq]
  (let [helper (fn [some-seq]
                 (if (or (empty? some-seq) (empty? (rest some-seq)))
                   (first some-seq)
                   (recur (rest some-seq))))]
    (helper a-seq)))

(defn seq= [seq1 seq2]
  (let [helper (fn [some-seq1 some-seq2]
                 (cond
                   (and (empty? some-seq1) (empty? some-seq2)) true
                   (or  (empty? some-seq1) (empty? some-seq2)) false
                   (or  (not (= (first some-seq1) (first some-seq2)))) false
                   :else
                   (recur (rest some-seq1) (rest some-seq2))))]
    (helper seq1 seq2)))

(defn find-first-index [pred a-seq]
  (loop [acc 0
         some-seq a-seq]
    (cond
      (empty? some-seq) nil
      (pred (first some-seq)) acc
      :else
      (recur (inc acc) (rest some-seq)))))

(defn avg [a-seq]
  (loop [acc      0
         some-seq a-seq
         n        0]
    (if (empty? some-seq)
      (/ acc n)
      (recur (+ acc (first some-seq)) (rest some-seq) (inc n)))))

(defn parity [a-seq]
  (loop [some-seq a-seq
         acc      #{}]
    (cond
      (empty? some-seq) acc
      (contains? acc (first some-seq)) (recur (rest some-seq) (disj acc (first some-seq)))
      :else
      (recur (rest some-seq) (conj acc (first some-seq))))))

(defn fast-fibo [n]
  (loop [n n
         fib-1 0
         fib-2  1]
    (if (< n 1)
      fib-1
      (recur (dec n) fib-2 (+ fib-1 fib-2)))))

(defn cut-at-repetition [a-seq]
  (loop [new-vec  []
         some-seq a-seq]
    (cond
      (empty? some-seq) new-vec
      (contains? (set new-vec) (first some-seq)) new-vec
      :else
      (recur (conj new-vec (first some-seq)) (rest some-seq)))))
