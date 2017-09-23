(ns bring-them-hell.db
  (:require [qbits.alia :as alia]
            [schema.core :as s]
            [cheshire.core :as json]
            [bring-them-hell.repr :as repr]))

(s/defn insert-monkey-task
  [db-details :- repr/CassandraDbDetails
   monkey-task :- repr/MonkeyTask]
  (let [session (alia/connect (alia/cluster {:contact-points [(db-details :ip)]}))]
    (alia/execute session (str "USE " (db-details :keyspace)))
    (alia/execute session (str "INSERT INTO monkey_tasks JSON '" (json/generate-string monkey-task) "'"))))

(s/defn get-all-monkey-tasks
  [db-details :- repr/CassandraDbDetails]
  (let [session (alia/connect (alia/cluster {:contact-points [(db-details :ip)]}))
        table "monkey_tasks"]
    (->> (db-details :keyspace)
         (str "USE ")
         (alia/execute session))
    (->> (str "SELECT * from " table)
         (alia/execute session))))
