(ns simple-timbre-kafka-appender.core
  (:require [taoensso.timbre :as timbre]
            [cheshire.core :as json])
  (:import (org.apache.kafka.clients.producer KafkaProducer ProducerRecord)
           (java.util Properties)))

(def iso-format "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

(defn log-data->json
  [data opts]
  (let [stacktrace-str (if-let [pr (:pr-stacktrace opts)]
                         #(with-out-str (pr %))
                         #(timbre/stacktrace % {:stacktrace-fonts {}}))]
    (json/generate-string
     (merge (:context data)
            {:level (:level data)
             :namespace (:?ns-str data)
             :file (:?file data)
             :line (:?line data)
             :stacktrace (some-> (:?err data) (stacktrace-str))
             :hostname (force (:hostname_ data))
             :message (force (:msg_ data))
             "@timestamp" (:instant data)})
     (merge {:date-format iso-format
             :pretty false}
            opts))))

(defn- make-properties [map] (doto (Properties.) (.putAll map)))

(defn- make-producer [kafka-bootstrap-servers]
  (-> {"bootstrap.servers" kafka-bootstrap-servers
       "acks" "all"
       "linger.ms" "1"
       "buffer.memory" "4194304"
       "key.serializer" "org.apache.kafka.common.serialization.ByteArraySerializer"
       "value.serializer" "org.apache.kafka.common.serialization.ByteArraySerializer"}
      (make-properties)
      (KafkaProducer.))) ;; json-serializer json-serializer)))

(defn kafka-appender
  [kafka-bootstrap-servers topic-name]
  (let [p (make-producer kafka-bootstrap-servers)]
    (fn [m]
      (let [output (.getBytes (log-data->json m nil) "UTF-8")
            record (ProducerRecord. topic-name output)]
        (.send p record)))))

(comment
  (defn test-timbre-info []
    (let [bootstrap-servers ["localhost:9092"]
          topic-name "test"]
      (timbre/merge-config! {:level :info
                             :output-fn (partial timbre/default-output-fn {:stacktrace-fonts {}}) ; disable color on stacktrace
                             :appenders {:kafka {:enabled? true
                                                 :async? true
                                                 ;; :output-fn  :inherit
                                                 :fn (kafka-appender bootstrap-servers topic-name)}}})
      (timbre/info "test info")))
  )