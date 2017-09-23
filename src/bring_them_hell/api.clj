(ns bring-them-hell.api
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [immuconf.config :as config]
            [cheshire.core :as json]
            [qbits.alia :as alia]
            [schema.core :as s]
            [bring-them-hell.util :as util]
            [bring-them-hell.repr :as repr]))

;; Configuration file
(defn load-config-file
  []
  (config/load))

(def my-config (load-config-file))

;; Database things
(s/defn insert-monkey-task-into-db
  [db-details :- repr/CassandraDbDetails
   monkey-task :- repr/MonkeyTask]
  (let [session (alia/connect (alia/cluster {:contact-points [(db-details :ip)]}))]
    (alia/execute session (str "USE " (db-details :keyspace)))
    (alia/execute session (str "INSERT INTO monkey_tasks JSON '" (json/generate-string monkey-task) "'"))))

(s/defn get-all-monkey-tasks
  [db-details :- repr/CassandraDbDetails]
  (let [session (alia/connect (alia/cluster {:contact-points [(db-details :ip)]}))
        table "monkey_tasks"]
    (alia/execute session (str "USE " (db-details :keyspace)))
    (alia/execute session "SELECT * from monkey_tasks")))

;; Main - App
(def app
  (api
   {:swagger
    {:ui   "/"
     :spec "/swagger.json"
     :data {:info {:title       "Bring-them-hell-api"
                   :description "Compojure Api example"}
            :tags [
                   {:name "api", :description "Put your Chaos Monkey On !"}
                   {:name "mon", :description "Just some monitoring stuff"}]}}}

   (context "/mon" []
            :tags ["mon"]

            (GET "/ping" []
                 :return {:result String}
                 :summary "Checks if the application is running"
                 (ok {:result "pong"})))

   (context "/api" []
            :tags ["api"]
            (GET "/monkey/tasks" []
                 :return [repr/MonkeyTask]
                 :summary "Return all tasks in the DB"
                 (ok (get-all-monkey-tasks (my-config :cassandra))))

            (POST "/monkey/task/new" []
                  :return repr/MonkeyTask
                  :body [monkey-task repr/MonkeyTask]
                  :summary "Create your monkey task"
                  (let [monkey-task (util/generate-uuid-in-task-if-needed monkey-task)]
                    (insert-monkey-task-into-db (my-config :cassandra) monkey-task)
                    (ok monkey-task))))))

