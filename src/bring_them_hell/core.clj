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
  []
  (let [session (alia/connect (alia/cluster {:contact-points ["192.168.99.100"]}))
        table "monkey_tasks"]
    (alia/execute session "USE bring_them_hell_dev")
    (alia/execute session "SELECT * from monkey_tasks")))

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
  (->> (get-all-tasks)
       (apply :nodes)
       (rand-nth) ; return {:username "USERNAME", :hostname "HOSTNAME"}
       (ssh-execute-cmd "reboot" (config/get my-cfg :ssh :private-key-path))
       (str "[REBOOT] - Chaos is here ! NODE: ")
       (println)))
