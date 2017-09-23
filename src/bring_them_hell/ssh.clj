(ns bring-them-hell.ssh
  (:require [clj-ssh.ssh :refer [ssh-agent session with-connection ssh add-identity]]))

(defn execute-cmd
  [cmd private-key-path {:keys [username hostname]}]
  (let [agent (ssh-agent {})]
    (add-identity agent {:private-key-path private-key-path})
    (let [session (session agent hostname {:strict-host-key-checking :no :username username})]
      (with-connection session
        (let [result (ssh session {:cmd cmd})]
          (println (result :out))))))
  hostname)


