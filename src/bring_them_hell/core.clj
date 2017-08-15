(ns bring-them-hell.core
  (:require [overtone.at-at :as at]
            [immuconf.config :as config]
            [clj-ssh.ssh :refer [ssh-agent session with-connection ssh add-identity]]
            [cheshire.core :as json]
            [swiss.arrows :refer :all]
            [qbits.alia :as alia]
            [clj-time.local :as time]))

;; Databases
(defn get-all-tasks
  [{:keys [ip keyspace]}]
  (let [session (alia/connect (alia/cluster {:contact-points [ip]}))
        table "monkey_tasks"]
    (->> (str "USE " keyspace)
         (alia/execute session))
    (->> (str "SELECT * from " table)
         (alia/execute session))))

;; Time things
(defn get-local-now
  []
  (time/format-local-time (time/local-now) :hour-minute-second))

(defn print-time-every-sec
  []
  (def my-pool (at/mk-pool))
  (at/every 1000 #(println (get-local-now)) my-pool))

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

(defn -main
  [& args]
  (println "Bring them Hell !")
  (def my-cfg (config/load))
  (->> (config/get my-cfg :cassandra)
       (get-all-tasks)
       (apply :nodes)
       (rand-nth) ; return {:username "USERNAME", :hostname "HOSTNAME"}
       (ssh-execute-cmd "reboot" (config/get my-cfg :ssh :private-key-path))
       (str "[REBOOT] - Chaos is here ! NODE: ")
       (println)))
