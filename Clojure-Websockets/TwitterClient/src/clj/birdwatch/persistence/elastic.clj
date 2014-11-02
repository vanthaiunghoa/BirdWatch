(ns birdwatch.persistence.elastic
  (:gen-class)
  (:require
   [birdwatch.data :as d]
   [birdwatch.persistence.tools :as pt]
   [clojure.tools.logging :as log]
   [clojure.pprint :as pp]
   [clojurewerkz.elastisch.rest             :as esr]
   [clojurewerkz.elastisch.rest.document    :as esd]
   [clojure.core.async :as async :refer [<! chan put! timeout go-loop]]))

(defn run-persistence-loop
  "run loop for persisting tweets"
  [persistence-chan conf conn]
  (go-loop [] (let [t (<! persistence-chan)]
                (try
                  (esd/put conn (:es-index conf) "tweet" (:id_str t) t)
                  (catch Exception ex (log/error ex "esd/put error"))))
           (recur)))

(defn run-rt-persistence-loop
  "run loop for persisting retweets"
  [rt-persistence-chan persistence-chan]
  (go-loop [] (when-let [rt (:retweeted_status (<! rt-persistence-chan))]
                (put! persistence-chan rt))
           (recur)))
