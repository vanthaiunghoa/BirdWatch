(defproject birdwatch "0.2.0-SNAPSHOT"
  :description "Main part of the BirdWatch system (without TwitterClient)"
  :url "https://github.com/matthiasn/Birdwatch"
  :license {:name "GNU AFFERO GENERAL PUBLIC LICENSE"
            :url  "https://www.gnu.org/licenses/agpl-3.0.en.html"}
  :dependencies [[org.clojure/clojure "1.9.0-RC1"]
                 [org.clojure/clojurescript "1.9.946"]
                 [clojurewerkz/elastisch "2.2.2" :exclusions [commons-io]]
                 [com.rpl/specter "1.0.5"]
                 [org.clojure/tools.logging "0.4.0"]
                 [com.taoensso/timbre "4.10.0"]
                 [com.taoensso/encore "2.92.0"]
                 [com.taoensso/carmine "2.16.0"]
                 [matthiasn/systems-toolbox "0.6.26"]
                 [matthiasn/systems-toolbox-sente "0.6.19"]
                 [matthiasn/systems-toolbox-redis "0.6.4"]
                 [matthiasn/systems-toolbox-kafka "0.6.13"]
                 [matthiasn/birdwatch-specs "0.3.2"]
                 [matthiasn/systemd-watchdog "0.1.3"]
                 [re-frame "0.9.4"]
                 [org.clojure/tools.namespace "0.2.11"]
                 [ch.qos.logback/logback-classic "1.2.3"]
                 [hiccup "1.0.5"]
                 [garden "1.2.5"]
                 [clj-time "0.14.2"]
                 [org.bouncycastle/bcprov-jdk15on "1.58"]
                 [pandect "0.6.1"]
                 [amalloy/ring-gzip-middleware "0.1.3"]
                 [tailrecursion/cljs-priority-map "1.2.0"]
                 [org.clojure/data.priority-map "0.0.7"]
                 [cljsjs/moment "2.17.1-1"]
                 [org.webjars.bower/purecss "0.6.2"]
                 [org.webjars.bower/d3 "3.5.17"]
                 [org.webjars.bower/d3-cloud "1.2.4"]
                 [clj-pid "0.1.2"]
                 [ring/ring-ssl "0.3.0" :exclusions [ring/ring-core]]
                 [metrics-clojure "2.9.0"]]

  :source-paths ["src/cljc" "src/clj/"]

  :jvm-opts ["-Xmx1G" "-server" "-Djdk.tls.ephemeralDHKeySize=2048"
             "-Djava.security.properties=TLS/birdwatch.security"]

  :main ^:skip-aot birdwatch.main
  :target-path "target/%s"

  :profiles
  {:uberjar {:aot   :all
             :auto-clean false}
   :http2   {:jvm-opts ["-Xbootclasspath/p:TLS/alpn-boot-8.1.7.v20160121.jar"]}}

  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-figwheel "0.5.14"]
            [lein-ancient "0.6.14"]
            [lein-sassy "1.0.8"
             :exclusions [org.clojure/clojure org.codehaus.plexus/plexus-utils]]
            [codox "0.10.3"]]

  :sass {:src "src/scss/"
         :dst "resources/public/css/"}

  :figwheel {:server-port 3452
             :css-dirs    ["resources/public/css"]}

  :clean-targets ^{:protect false} ["resources/public/js/build/" "target/"]

  :cljsbuild
  {:builds [{:id           "dev"
             :source-paths ["src/cljc" "src/cljs" "env/dev/cljs"]
             :figwheel     true
             :compiler     {:main          "birdwatch.dev"
                            :asset-path    "js/build"
                            :optimizations :none
                            :output-dir    "resources/public/js/build/"
                            :output-to     "resources/public/js/build/birdwatch.js"
                            :source-map    true
                            :pretty-print  true}}
            {:id           "release"
             :source-paths ["src/cljc" "src/cljs"]
             :compiler     {:output-to     "resources/public/js/build/birdwatch.js"
                            :optimizations :advanced
                            :externs       ["externs/misc.js"]
                            :pretty-print  false}}]})
