(ns cascalog-demo.demo
  (:use cascalog.api)
  (:require [cascalog.logic [vars :as v] [ops :as c]])
  (:gen-class))

(defn textline-parsed [dir num-fields]
  (let [outargs (v/gen-nullable-vars num-fields)
        source (hfs-textline dir)]
    (<- outargs (source ?line) ((c/re-parse #"[^\s]+") ?line :>> outargs) (:distinct false))))

(defn to-long [num] (Long/parseLong num))

(defn follows-data [dir] (textline-parsed dir 2))

(defn action-data [dir]
  (let [source (textline-parsed dir 3)]
    (<- [?person ?action ?time] (source ?person ?action ?time-str)
                                (to-long ?time-str :> ?time) (:distinct false))))

(defbufferop mk-feed [tuples]
  [(pr-str (take 5 tuples))])

(defn action-score [now-ms followers time-ms]
  (let [days-delta (div (- now-ms time-ms) 86400000)]
    (div followers (+ days-delta 1))))

(defn compute-news-feed [output-tap follows-dir action-dir]
  (let [follows (follows-data follows-dir)
        action (action-data action-dir)
        follower-count (<- [?person ?count] (follows ?person2 ?person) (c/count ?count))]
    (?<- output-tap [?person ?feed] (follows ?person ?person2) (action ?person2 ?action ?time)
                               (follower-count ?person2 ?followers)
                               (action-score (System/currentTimeMillis) ?followers ?time :> ?score)
                               (:sort ?score) (:reverse true)
                               (mk-feed ?person2 ?action ?time :> ?feed))))

(defn -main [follows-dir action-dir output-dir]
  (compute-news-feed (hfs-textline output-dir) follows-dir action-dir))

