(defproject cascalog-demo "1.0.0-SNAPSHOT"
  :source-paths ["src/clj"]
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [cascalog "2.1.1"]
                 [org.apache.hadoop/hadoop-client "2.5.2"]]
  :aot [cascalog-demo.demo]
  :main cascalog-demo.demo
)

