(ns bring-them-hell.core
  (:require [overtone.at-at :as at]
            [immuconf.config :as config]
            [clojure.tools.logging :as log]
            [clj-ssh.ssh :refer [ssh-agent session with-connection ssh add-identity]]
            [cheshire.core :as json]
            [swiss.arrows :refer :all]
            [qbits.alia :as alia]))

(def my-pool (at/mk-pool))
(def my-cfg (config/load))

;; Databases
(defn get-all-tasks
  [{:keys [ip keyspace]}]
  (let [session (alia/connect (alia/cluster {:contact-points [ip]}))
        table "monkey_tasks"]
    (->> (str "USE " keyspace)
         (alia/execute session))
    (->> (str "SELECT * from " table)
         (alia/execute session))))

;; SSH things
(defn ssh-execute-cmd
  [cmd private-key-path {:keys [username hostname]}]
  (let [agent (ssh-agent {})]
    (add-identity agent {:private-key-path private-key-path})
    (let [session (session agent hostname {:strict-host-key-checking :no :username username})]
      (with-connection session
        (let [result (ssh session {:cmd cmd})]
          (println (result :out))))))
  hostname)

(defn exec-monkey-task
  [monkey-task]
  (->> (monkey-task :uuid)
       (log/info "[EXECUTING] tasks UUID:"))
  (->> (:nodes monkey-task)
       (rand-nth)
       (ssh-execute-cmd "reboot" (config/get my-cfg :ssh :private-key-path))
       (log/info "[CHAOS] reboot node:")))

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
       (get-all-tasks)
       (compute-monkey-tasks)))
