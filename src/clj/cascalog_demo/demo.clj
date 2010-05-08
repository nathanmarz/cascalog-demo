(ns cascalog-demo.demo
  (:use cascalog.api)
  (:require [cascalog [workflow :as w] [predicate :as p] [vars :as v] [ops :as c]])
  (:gen-class))

(defn textline-parsed [dir num-fields]
  (let [outargs (v/gen-nullable-vars num-fields)
        source (hfs-textline dir)]
    (<- outargs (source ?line) (c/re-parse [#"[^\s]+"] ?line :>> outargs) (:distinct false))))

(defn to-long [num] (Long/parseLong num))

(defn follows-data [dir] (textline-parsed dir 2))

(defn action-data [dir]
  (let [source (textline-parsed dir 3)]
    (<- [?person ?action ?time] (source ?person ?action ?time-str)
                                (to-long ?time-str :> ?time) (:distinct false))))

(w/defbufferop mk-feed [tuples]
  [(pr-str (take 5 tuples))])

(defn action-score [now-ms folls time-ms]
  (let [days-delta (div (- now-ms time-ms) 86400000)]
    (div folls (+ days-delta 1))))

(defn compute-news-feed [output-tap follows-dir action-dir]
  (let [follows (follows-data follows-dir)
        action (action-data action-dir)
        follower-count (<- [?p ?c] (follows ?p2 ?p) (c/count ?c))]
    (?<- output-tap [?p ?feed] (follows ?p ?p2) (action ?p2 ?action ?time)
                               (follower-count ?p2 ?folls)
                               (action-score (System/currentTimeMillis) ?folls ?time :> ?score)
                               (:sort ?score) (:reverse true)
                               (mk-feed ?p2 ?action ?time :> ?feed))))

(defn -main [follows-dir action-dir output-dir]
  (compute-news-feed (hfs-textline output-dir) follows-dir action-dir))

