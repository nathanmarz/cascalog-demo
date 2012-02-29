(defproject cascalog-demo "1.0.0-SNAPSHOT"
  :source-path "src/clj"
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [cascalog "1.8.7-SNAPSHOT"]
                 ]
  :dev-dependencies [[org.apache.hadoop/hadoop-core "0.20.2-dev"]]
  :namespaces [cascalog-demo.demo])
