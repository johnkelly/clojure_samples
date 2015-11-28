(ns recursion)

(defn product [coll]
  (if (empty? coll)
    1
    (* (first coll) (product (rest coll)))))

(defn singleton? [coll]
  (and (not (empty? coll)) (empty? (rest coll))))

(defn my-last [coll]
  (if (or (singleton? coll) (empty? coll))
    (first coll)
    (my-last (rest coll))))

(defn max-element [a-seq]
  (if (or (empty? a-seq) (singleton? a-seq))
    (first a-seq)
    (max (first a-seq) (max-element (rest a-seq)))))

(defn seq-max [seq-1 seq-2]
  (if (>= (count seq-2) (count seq-1)) seq-2 seq-1))

(defn longest-sequence [a-seq]
  (if (or (singleton? a-seq) (empty? a-seq))
    (first a-seq)
    (seq-max (first a-seq) (longest-sequence (rest a-seq)))))

(defn my-filter [pred? a-seq]
  (if (empty? a-seq)
    a-seq
    (let [first (first a-seq)
          rest  (my-filter pred? (rest a-seq))]
      (if (pred? first) (cons first rest) rest))))

(defn sequence-contains? [elem a-seq]
  (cond
    (empty? a-seq) false
    (= elem (first a-seq)) true
    :else
      (sequence-contains? elem (rest a-seq))))

(defn my-take-while [pred? a-seq]
  (cond
    (empty? a-seq) a-seq
    (pred? (first a-seq)) (cons (first a-seq) (my-take-while pred? (rest a-seq)))
    :else
     '()))

(defn my-drop-while [pred? a-seq]
  (cond
    (empty? a-seq) a-seq
    (pred? (first a-seq)) (my-drop-while pred? (rest a-seq))
    :else
    a-seq))

(defn seq= [a-seq b-seq]
  (cond
    (and (empty? a-seq) (empty? b-seq)) true
    (or  (empty? a-seq) (empty? b-seq)) false
    (not (= (first a-seq) (first b-seq))) false
    :else
    (seq= (rest a-seq) (rest b-seq))))

(defn my-map [f seq-1 seq-2]
  (if (or  (empty? seq-1) (empty? seq-2))
    '()
    (cons (f (first seq-1) (first seq-2)) (my-map f (rest seq-1) (rest seq-2)))))

(defn power [n k]
  (if (== 0 k)
    1
    (* n (power n (dec k)))))

(defn fib [n]
  (cond
    (== 0 n) 0
    (== 1 n) 1
    :else
      (+ (fib (- n 1)) (fib (- n 2)))))

(defn my-repeat [how-many-times what-to-repeat]
  (if (>= how-many-times 1)
    (cons what-to-repeat (my-repeat (dec how-many-times) what-to-repeat))
    '()))

(defn my-range [up-to]
  (if (== up-to 0)
    '()
    (cons (dec up-to) (my-range (dec up-to)))))

(defn tails [a-seq]
  (if (empty? a-seq)
    (cons a-seq '())
    (cons a-seq (tails (drop 1 (seq a-seq))))))

(defn inits [a-seq]
  (map reverse (reverse (tails (reverse a-seq)))))

(defn rotations [a-seq]
  (seq (set (map concat (tails a-seq) (inits a-seq)))))

(defn my-frequencies-helper [freqs a-seq]
  (if (empty? a-seq)
    freqs
    (let [element       (first a-seq)
          element-count (inc (get freqs element 0))]
      (my-frequencies-helper
        (assoc freqs element element-count)
        (rest a-seq)))))

(defn my-frequencies [a-seq]
  (my-frequencies-helper {} a-seq))

(defn un-frequencies [a-map]
  (if (empty? a-map)
    '()
    (let [m-key (key (first a-map))
          m-val (val (first a-map))]
      (concat (repeat m-val m-key) (un-frequencies(rest a-map))))))

(defn my-take [n coll]
  (if (and (> n 0) (not (empty? coll)))
    (cons (first coll) (my-take (dec n) (rest coll)))
    '()))

(defn my-drop [n coll]
  (if (> n 0)
    (my-drop (dec n) (rest coll))
    coll))

(defn halve [a-seq]
  (let [split-index (int (/ (count a-seq) 2))]
    [(my-take split-index a-seq) (my-drop split-index a-seq)]))

(defn seq-merge [a-seq b-seq]
  (let [current-a (first a-seq)
        current-b (first b-seq)]
    (cond
      (empty? a-seq) b-seq
      (empty? b-seq) a-seq
      (<= current-a current-b) (cons current-a (seq-merge (rest a-seq) b-seq))
      (> current-a current-b)  (cons current-b (seq-merge a-seq (rest b-seq))))))

(defn merge-sort [a-seq]
  (if (or (empty? a-seq) (singleton? a-seq))
    a-seq
    (let [[half-1 half-2] (halve a-seq)]
      (seq-merge (merge-sort half-1) (merge-sort half-2)))))

(defn split-into-monotonics [a-seq]
  [:-])

(defn permutations [a-set]
  [:-])


(set [1 5 3])

(permutations #{})
;=> (())
(permutations #{1 5 3})
;=> ((1 5 3) (5 1 3) (5 3 1) (1 3 5) (3 1 5) (3 5 1))

(defn powerset [a-set]
  [:-])
