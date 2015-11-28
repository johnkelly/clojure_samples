(ns hello-nlp.core
  (use opennlp.nlp)
  (use opennlp.treebank)
  (use opennlp.tools.train)
  (use opennlp.tools.filters)
  (use amazonica.aws.sqs)
  (:require [clj-wordnet.core    :refer :all])
  (:require [taoensso.faraday :as far]))

(def wordnet (make-dictionary "dict/"))

(def get-sentences
  (make-sentence-detector "models/en-sent.bin"))

(def tokenize
  (make-tokenizer "models/en-token.bin"))

(def pos-tag
  (make-pos-tagger "models/en-pos-maxent.bin"))

(get-sentences "I like reading books. And I like watching goo movies")

(map tokenize (get-sentences "I like reading books. And I like watching goo movies"))

(mapcat pos-tag (map tokenize (get-sentences "I like reading books. And I like watching two movies back to back.")))

(defn cardinal? [tagged-token]
  (let [part-of-speech (second tagged-token)]
    (contains? #{"CD" "JJ"} (str part-of-speech))))

(defn verb? [tagged-token]
  (let [part-of-speech (second tagged-token)]
    (contains? #{"VB" "VBD" "VBG" "VBN" "VBP" "VBZ"} (str part-of-speech))))

(defn noun? [tagged-token]
  (let [part-of-speech (second tagged-token)]
    (contains? #{"NN" "NNP" "NNPS" "NNS"} (str part-of-speech))))

(defn relevant? [tagged-token]
  (or (cardinal? tagged-token)
      (verb?     tagged-token)
      (noun?     tagged-token)))

(defn relevant-words [word-tokens]
  (filter relevant? tokens))

(def question
  "How many dogs do I have if I bought two and one died?")

(def tokens
  (pos-tag (tokenize question)))

(defn to-simple-pos [tagged-token]
  (cond
    (noun?     tagged-token) [(first tagged-token) :noun]
    (verb?     tagged-token) [(first tagged-token) :verb]
    (cardinal? tagged-token) [(first tagged-token) :number]))

(defn transform-to-root [simple-token]
  (if (= (second simple-token) :number)
    simple-token
    (let [word (first   simple-token)
          pos  (second  simple-token)]
      [(:lemma (first (wordnet word pos))) pos])))

;(["How" "WRB"] ["many" "JJ"] ["dogs" "NNS"] ["do" "VBP"] ["I" "PRP"] ["have" "VB"] ["if" "IN"] ["I" "PRP"] ["buy" "VBP"] ["two" "CD"] ["and" "CC"] ["sell" "VB"] ["one" "CD"] ["?" "."])

(filter cardinal? tokens)
(filter verb? tokens)
(filter noun? tokens)
(filter relevant? tokens)

(relevant-words tokens)
;(["many" "JJ"] ["dogs" "NNS"] ["do" "VBP"] ["have" "VB"] ["buy" "VBP"] ["two" "CD"] ["sell" "VB"] ["one" "CD"])

(map to-simple-pos (relevant-words tokens))

;(["many" :number] ["dogs" :noun] ["do" :verb] ["have" :verb] ["buy" :verb] ["two" :number] ["sell" :verb] ["one" :number])

(map transform-to-root (map to-simple-pos (relevant-words tokens)))
;(["many" :number] ["dog" :noun] ["do" :verb] ["have" :verb] ["buy" :verb] ["two" :number] ["sell" :verb] ["one" :number])

(map ->word-root (relevant-words tokens))

(:lemma ran)
(:gloss ran)



;;Dynamo DB
(def client-opts
  {:access-key "BLAH"
   :secret-key "WAT"
   :endpoint "http://localhost:8000"})

(far/list-tables client-opts)

(far/create-table client-opts :my-table
                  [:id :n]
                  {:throughput {:read 1 :write 1}
                   :block? true
                   })

(far/put-item client-opts
              :my-table
              {:id 0
               :name "Steve" :age 22 :data (far/freeze {:vector [1 2 3]
                                                        :set    #{1 2 3}
                                                        :rational (/ 22 7)})})

(far/get-item client-opts :my-table {:id 0})




(defn number?
  "Returns whether the word is a number"
  [x]
  (contains? #{"1" "2" "3" "4" "5" "6" "7" "8" "9"} x))

(defn filter-numbers [string]
  (filter (fn [x] (number? x)) (mapcat tokenize (get-sentences "I like to read 2 books a week. What the fuck is this song?")))

(nouns (mapcat pos-tag (map tokenize (get-sentences "I like reading books. And I like watching goo movies"))))

(def doccat-model (train-document-categorization "training/emotion.train"))
(def doccat (make-document-categorizer doccat-model))
(time (meta (doccat (clojure.string/lower-case "Hey Keren! This week has been great but I'm glad the weekend is here. I'm disappointed you can't make it tomorrow but I appreciate the day notice. It's no big deal. I'm the same way when it comes to work/school so I completely understand. It sounds like maybe sometime next weekend might be better when your exams are over?"))))


(mapcat pos-tag (map tokenize (get-sentences (.toLowerCase "Add 4 + 4"))))



;What is the result when 436921 is rounded to the nearest thousand and then expressed in scientific notation?

;What is the average age of people ages 17, 24, 25?

;The positive integer n is not divisible by 7. The remainder when n^2 is divided by 7 and the remainder when n is divided by 7 are each equal to k. What is k?

;The set S consists of all multiples of 6. Which of the following sets are contained within S?
;Roman numeral 1.   The set of all multiples of 3
;Roman numeral 2.  The set of all multiples of 9
;Roman numeral 3. The set of all multiples of 12

;At the beginning of 2006, both Alan and Dave were taller than Boris, and Boris was taller than Charles. During the year, Alan grew 2 inches, Boris and Dave each grew 4 inches, and Charles grew 3 inches. Of the following, which could NOT have been true at the beginning of 2007?

; If n is an integer and if n^2 is a positive integer, which of the following must also be a positive integer?
; (A) (n^2) + n
; (B) (2 times (n^2)) minus n
; (C) (n^2) minus (n^3)
; (D) (n^3) + n
; (E) (2 times (n^3)) + n


;;SQS
(def aws-cred {:access-key "AKIAILK5BNUR7OYTNLWA"
               :secret-key "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"
               :endpoint   "us-east-1"})

(list-queues aws-cred)

(def queue (find-queue aws-cred "Parse"))
(send-message aws-cred queue "Hello a fifth time from clojure")
(send-message aws-cred queue "Hello a sixth time from clojure")


(def message
  (receive-message aws-cred :queue-url queue
                            :wait-time-seconds 0
                            :max-number-of-messages 10 ))


(forever (print (receive-message aws-cred :queue-url queue)))
(forever (print (first (:messages (receive-message aws-cred
                                                   :queue-url              queue
                                                   :max-number-of-messages 10)))))


(defn next-message []
  (first (:message (receive-message aws-cred :queue-url queue))))


  (forever
    (let [messages (:messages (receive-message aws-cred
                                               :queue-url              queue
                                               :delete                 true
                                               :max-number-of-messages 10))]
      (pmap display-message messages)))

;;;Good Stuff

(defmacro forever [& body]
  `(while true ~@body))

(defn display-message [message]
  (if-not (= nil message)
          (:body message)))



;; Send messages to aws
(dotimes [n 1000] (future (send-message aws-cred queue (str "n is" n))))

  ;; Read messages from aws
  (forever
    (future
      (do
        (let [messages (:messages (receive-message aws-cred
                                                   :queue-url              queue
                                                   :delete                 true
                                                   :max-number-of-messages 10))]
          (map display-message messages))
        (Thread/sleep 500))))



(pmap #(print-str %) [1 2 3 4 5 6 7 8 9 10])
(display-message {:body "WTF"})

(do
  (print "WHAT")
  (print "THAT"))

