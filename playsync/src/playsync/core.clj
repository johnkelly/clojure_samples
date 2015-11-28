(ns playsync.core
    (:require [clojure.core.async
               :as a
               :refer [>! <! >!! <!! go chan buffer close! thread
                       alts! alts!! timeout]]))

(def echo-chan (chan))

(go (println (<! echo-chan)))
(>!! echo-chan "ketchup")
