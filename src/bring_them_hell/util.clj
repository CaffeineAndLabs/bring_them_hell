(ns bring-them-hell.util
  (:require [cheshire.core :as json]
            [qbits.alia :as alia]
            [schema.core :as s]
            [bring-them-hell.repr :as repr]))

(defn generate-random-uuid
  []
  (java.util.UUID/randomUUID))

(s/defn generate-uuid-in-task-if-needed :- repr/MonkeyTask
  "Ensure that :uuid is present in MonkeyTask schema"
  [monkey-task :- repr/MonkeyTask]
  (if (contains? monkey-task :uuid)
    monkey-task
    (assoc monkey-task :uuid (generate-random-uuid))))

