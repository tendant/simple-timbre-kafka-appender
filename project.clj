(defproject simple-timbre-kafka-appender "0.1.1-SNAPSHOT"
  :description "Yet another simple timbre kafka appender"
  :url "https://github.com/tendant/simple-timbre-kafka-appender"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.apache.kafka/kafka-clients "0.11.0.0"]
                 [com.taoensso/timbre "4.10.0"]
                 [cheshire "5.8.0"]])
