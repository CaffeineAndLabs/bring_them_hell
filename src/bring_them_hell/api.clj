(ns bring-them-hell.api
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [immuconf.config :as config]
            [cheshire.core :as json]
            [qbits.alia :as alia]
            [schema.core :as s]
            [bring-them-hell.util :as util]
            [bring-them-hell.db :as db]
            [bring-them-hell.repr :as repr]))

;; Configuration file
(defn load-config-file
  []
  (config/load))

(def my-config (load-config-file))

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
                 (ok (db/get-all-monkey-tasks (my-config :cassandra))))

            (POST "/monkey/task/new" []
                  :return repr/MonkeyTask
                  :body [monkey-task repr/MonkeyTask]
                  :summary "Create your monkey task"
                  (let [monkey-task (util/generate-uuid-in-task-if-needed monkey-task)]
                    (db/insert-monkey-task (my-config :cassandra) monkey-task)
                    (ok monkey-task))))))

