(ns bring-them-hell.api
  (:require [cheshire.core :as json]
            [schema.core :as s]
            [clojure.test :refer :all]
            [bring-them-hell-api.handler :refer :all]
            [ring.mock.request :as mock]))

(defn parse-body [body]
  (json/parse-string (slurp body) true))

(deftest api-returns
  (testing "Test if /mon/ping returns expected response"
    (let [response (app (-> (mock/request :get "/mon/ping")))
         body (parse-body (:body response))]
      (is (= (:status response) 200))
      (is (= (:result body) "pong"))))

  (testing "Test GET request to /monkey/tasks returns 200"
    (let [response (app (-> (mock/request :get "/api/monkey/tasks")))
          body (parse-body (:body response))]
      (is (= (:status response) 200))))

  (testing "Test POST request to /monkey/task/new returns expected response"
    (let [uuid "96cfb459-b83d-472c-b4ee-33ae201345cf"
          nodes-details (s/validate [RemoteNodeDetails] [{:hostname "192.168.1.2", :username "root"}])
          monkey-task (s/validate MonkeyTask {:uuid (java.util.UUID/fromString uuid)
                                              :name "Test"
                                              :nodes nodes-details
                                              :type :reboot})
          response (app (-> (mock/request :post "/api/monkey/task/new")
                            (mock/content-type "application/json")
                            (mock/body (json/generate-string monkey-task))))
          body (parse-body (:body response))]
      (is (= (:status response) 200))
      (is (= body {:uuid uuid
                   :name "Test"
                   :nodes nodes-details
                   :type "reboot"})))))

(deftest monkey-task-schema
  (testing "Test if monkey-task is not modify when a :uuid is provided"
    (let [nodes-details (s/validate [RemoteNodeDetails] [{:hostname "192.168.1.2", :username "root"}])
          monkey-task (s/validate MonkeyTask {:uuid (generate-random-uuid),
                                              :name "SuperTask",
                                              :nodes nodes-details
                                              :type :reboot})]
      (is (= (generate-uuid-in-task-if-needed monkey-task) monkey-task))))

  (testing "Test if uuid is correctly added according to MonkeyTask schema (when not provided in the POST)"
    (let [nodes-details (s/validate [RemoteNodeDetails] [{:hostname "192.168.1.2", :username "root"}])
          monkey-task (s/validate MonkeyTask {:name "Test",
                                              :nodes nodes-details
                                              :type :reboot})]
      (with-redefs-fn {#'generate-random-uuid (fn [] (java.util.UUID/fromString "96cfb459-b83d-472c-b4ee-33ae201345cf"))}
        #(is (= (generate-uuid-in-task-if-needed monkey-task) (s/validate MonkeyTask {:uuid (generate-random-uuid)
                                                                                      :name "Test"
                                                                                      :nodes nodes-details
                                                                                      :type :reboot})))))))

(deftest monkey-tasks-database)
