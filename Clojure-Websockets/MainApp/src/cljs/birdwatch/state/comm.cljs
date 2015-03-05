(ns birdwatch.state.comm
  (:require-macros [cljs.core.async.macros :refer [go-loop]])
  (:require [birdwatch.state.search :as s]
            [birdwatch.state.initial :as i]
            [birdwatch.state.proc :as p]
            [cljs.core.async :refer [<! put! pipe timeout chan sliding-buffer]]
            [cljs.core.match :refer-macros [match]]))

;;;; Channels processing namespace. Here, messages are taken from channels and processed.

(defn- handle-prev-chunk
  "Take messages (vectors of tweets) from prev-chunks-chan, add each tweet to application
   state, then pause to give the event loop back to the application (otherwise, UI becomes
   unresponsive for a short while)."
  [chunk app]
  (doseq [t chunk] (p/add-tweet! t app)))

(defn handle-incoming
  "Handle incoming messages process / add to application state."
  [app put-fn msg]
  (match msg
         ;; tweet-related messages from server
         [:tweet/new tweet] (p/add-tweet! tweet app)
         [:tweet/missing-tweet tweet] (p/add-to-tweets-map! app :tweets-map tweet)
         [:tweet/prev-chunk chunk] (do (handle-prev-chunk chunk app) (s/load-prev app put-fn))

         ;; stats received 
         [:stats/users-count n] (swap! app assoc :users-count n)
         [:stats/total-tweet-count n] (swap! app assoc :total-tweet-count n)

         ;; command messages
         [:toggle-live] (swap! app update :live not)
         [:set-search-text text] (swap! app assoc :search-text text)
         [:set-current-page page] (swap! app assoc :page page)
         [:set-page-size n] (swap! app assoc :n n)
         [:start-search] (s/start-search app (i/initial-state) put-fn)
         [:set-sort-order by-order] (swap! app assoc :sorted by-order)
         [:retrieve-missing id-str] (put-fn [:cmd/missing {:id_str id-str}])
         [:retrieve-missing id-str] (put-fn [:cmd/missing {:id_str id-str}])
         [:append-search-text text] (s/append-search-text text app)

         :else (prn "unknown msg in data-loop" msg)))
