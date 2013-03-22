(defproject cascalog-demo "1.0.0-SNAPSHOT"
  :source-paths ["src/clj"]
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [cascalog "1.10.0"]
                 ]
  :profiles { :dev {:dependencies [[org.apache.hadoop/hadoop-core "0.20.2-dev"]]}}
  :aot [cascalog-demo.demo]
  :main cascalog-demo.demo
)

