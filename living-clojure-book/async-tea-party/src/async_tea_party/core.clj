(ns async-tea-party.core
    (:gen-class)
    (:require [clojure.core.async :as async]))

; (def tea-channel (async/chan 10))

; (async/>!! tea-channel :cup-of-tea)

; (async/<!! tea-channel)

; (async/>!! tea-channel :cup-of-tea-2)
; (async/>!! tea-channel :cup-of-tea-3)
; (async/>!! tea-channel :cup-of-tea-4)

; (async/close! tea-channel)

; (async/<!! tea-channel)
; (async/<!! tea-channel)
; (async/<!! tea-channel)


; (async/>!! tea-channel nil)

; (let [tea-channel (async/chan)]
;   (async/go (async/>! tea-channel :cup-of-tea-1))
;   (async/go (println "Thanks for the " (async/<! tea-channel))))

; (def tea-channel (async/chan 10))

; (async/go-loop []
;                (println "Thanks for the " (async/<! tea-channel))
;                (recur))

; (async/>!! tea-channel :hot-cup-of-tea)
; (async/>!! tea-channel :tea-with-sugar)
; (async/>!! tea-channel :tea-with-milk)
; (async/>!! tea-channel :hot-chocolate)


; (def tea-channel (async/chan 10))
; (def milk-channel (async/chan 10))
; (def sugar-channel (async/chan 10))

; (async/go-loop []
;                (let [[v ch] (async/alts! [tea-channel milk-channel sugar-channel])]
;                  (println "Got " v " from " ch)
;                  (recur)))

; (async/>!! sugar-channel :sugar)
; (async/>!! milk-channel  :milk)
; (async/>!! tea-channel   :tea)


(def google-tea-service-chan (async/chan 10))
(def yahoo-tea-service-chan  (async/chan 10))
(def result-chan             (async/chan 10))

(defn random-add []
  (reduce + (conj [] (repeat 1 (rand-int 100000)))))

(defn request-google-tea-service []
  (async/go
            (random-add)
    (async/>! google-tea-service-chan "tea compliments of google")))


(defn request-yahoo-tea-service []
  (async/go
            (random-add)
    (async/>! yahoo-tea-service-chan "tea compliments of yahoo")))

(defn request-tea []
  (request-google-tea-service)
  (request-yahoo-tea-service)
  (async/go (let [[v] (async/alts! [google-tea-service-chan yahoo-tea-service-chan])]
              (async/>! result-chan v))))


(defn -main [& args]
  (println "Requesting tea!")
  (request-tea)
  (println (async/<!! result-chan)))
