# simple-timbre-kafka-appender

A Clojure library designed to ... well, that part is up to you.

## Usage

```clojure
(let [bootstrap-servers ["localhost:9092"]
          topic-name "test"]
      (timbre/merge-config! {:level :info
                             :appenders {:kafka {:enabled? true
                                                 :async? true
                                                 ;; :output-fn  :inherit
                                                 :fn (kafka-appender bootstrap-servers topic-name)}}})
      (timbre/info "test info"))
```

## License

Copyright © 2017 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
