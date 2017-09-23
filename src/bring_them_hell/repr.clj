(ns bring-them-hell.repr
  (:require [schema.core :as s]))

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
