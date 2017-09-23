(defproject bring-them-hell "0.1.0-SNAPSHOT"
  :description "Bring Them Hell is a Chaos Monkey"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.logging "0.4.0"]
                 [ch.qos.logback/logback-classic "1.1.3"]
                 [clj-ssh "0.5.14"]
                 [overtone/at-at "1.2.0"]
                 [levand/immuconf "0.1.0"]
                 [swiss-arrows "1.0.0"]
                 [cc.qbits/alia-all "4.0.0"]
                 [cheshire "5.7.1"]
                 [metosin/schema-tools "0.9.0"]
                 [metosin/compojure-api "1.1.11"]
                 [clj-time "0.14.0"]]
  :main ^:skip-aot bring-them-hell.core
  :ring {:handler bring-them-hell.api/app}
  :target-path "target/%s"
  :profiles {:dev {:dependencies [[javax.servlet/javax.servlet-api "3.1.0"]
                                  [cheshire "5.7.1"]
                                  [ring/ring-mock "0.3.0"]]
                   :plugins [[lein-ring "0.12.0"]]}})
