(defproject bring-them-hell "0.1.0-SNAPSHOT"
  :description "Bring Them Hell is a Chaos Monkey"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [clj-ssh "0.5.14"]
                 [overtone/at-at "1.2.0"]
                 [levand/immuconf "0.1.0"]
                 [swiss-arrows "1.0.0"]
                 [cc.qbits/alia-all "4.0.0"]
                 [cheshire "5.7.1"]
                 [clj-time "0.14.0"]]
  :main ^:skip-aot bring-them-hell.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
