(ns bring-them-hell.api
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [immuconf.config :as config]
            [cheshire.core :as json]
            [qbits.alia :as alia]
            [schema.core :as s]))

;; Helper
(defn generate-random-uuid
  []
  (java.util.UUID/randomUUID))

;; Configuration file
(defn load-config-file
  []
  (config/load))

(def my-config (load-config-file))

;; Schemas for Configuration file
(s/defschema CassandraDbDetails
  {:ip s/Str
   :keyspace s/Str})

(s/defschema ConfigFile
  {:cassandra CassandraDbDetails})

;; Schemas
(s/defschema RemoteNodeDetails
  "Details for remote host candidate to Chaos"
  {:hostname s/Str
   :username s/Str})

(s/defschema MonkeyTask
  "A schema for a Monkey Task"
  {(s/optional-key :uuid) s/Uuid
   :name s/Str
   ;; NOTE: Use LIST<frozen<node>> type on cassandra side. (Prefer to use SET but issue with it)
   ;; ISSUE: a-clojure.lang.PersistentHashSet is use on clojure side to represent SET type (cassandra side)
   ;; PersistentHashSet does not implements Sequential
   ;; TODO: Dig in and check the deserialization of UDTValue (https://github.com/mpenet/alia/issues/45)
   :nodes [RemoteNodeDetails]
   :type (s/enum :reboot)})

(s/defn generate-uuid-in-task-if-needed :- MonkeyTask
  "Ensure that :uuid is present in MonkeyTask schema"
  [monkey-task :- MonkeyTask]
  (if (contains? monkey-task :uuid)
    monkey-task
    (assoc monkey-task :uuid (generate-random-uuid))))

;; Database things
(s/defn insert-monkey-task-into-db
  [db-details :- CassandraDbDetails
   monkey-task :- MonkeyTask]
  (let [session (alia/connect (alia/cluster {:contact-points [(db-details :ip)]}))]
    (alia/execute session (str "USE " (db-details :keyspace)))
    (alia/execute session (str "INSERT INTO monkey_tasks JSON '" (json/generate-string monkey-task) "'"))))

(s/defn get-all-monkey-tasks
  [db-details :- CassandraDbDetails]
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
                 :return [MonkeyTask]
                 :summary "Return all tasks in the DB"
                 (ok (get-all-monkey-tasks (my-config :cassandra))))

            (POST "/monkey/task/new" []
                  :return MonkeyTask
                  :body [monkey-task MonkeyTask]
                  :summary "Create your monkey task"
                  (let [monkey-task (generate-uuid-in-task-if-needed monkey-task)]
                    (insert-monkey-task-into-db (my-config :cassandra) monkey-task)
                    (ok monkey-task))))))

