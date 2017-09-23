(ns bring-them-hell.db
  (:require [qbits.alia :as alia]))

(defn get-all-tasks
  [{:keys [ip keyspace]}]
  (let [session (alia/connect (alia/cluster {:contact-points [ip]}))
        table "monkey_tasks"]
    (->> (str "USE " keyspace)
         (alia/execute session))
    (->> (str "SELECT * from " table)
         (alia/execute session))))
