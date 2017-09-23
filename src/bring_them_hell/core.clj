(ns bring-them-hell.core
  (:require [overtone.at-at :as at]
            [immuconf.config :as config]
            [clojure.tools.logging :as log]
            [cheshire.core :as json]
            [swiss.arrows :refer :all]
            [qbits.alia :as alia]
            [bring-them-hell.db :as db]
            [bring-them-hell.ssh :as ssh]))

(def my-pool (at/mk-pool))
(def my-cfg (config/load))

(defn exec-monkey-task
  [monkey-task]
  (->> (monkey-task :uuid)
       (log/info "[EXECUTING] tasks UUID:"))
  (->> (:nodes monkey-task)
       (rand-nth)
       (ssh/execute-cmd "reboot" (config/get my-cfg :ssh :private-key-path))
       (log/info "[CHAOS] reboot node: ")))

(defn cron-monkey-task
  [monkey-task]
  (->> (monkey-task :uuid)
       (log/info "[CREATE] cron for tasks UUID:"))
  (at/every 60000 #(exec-monkey-task monkey-task) my-pool))

(defn compute-monkey-tasks
  "Create cron tasks for every monkey tasks"
  [monkey-tasks]
  (apply cron-monkey-task monkey-tasks))

(defn -main
  [& args]
  (println "Bring them Hell !")
  (->> (config/get my-cfg :cassandra)
       (db/get-all-monkey-tasks)
       (compute-monkey-tasks)))
