(ns simpletodo.backend
  (:require [ring.adapter.jetty :as j]
            [simpletodo.api :as api]
            [clojure.data.json :as json]))

(defn todo-handler [request]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body (json/write-str (api/get-todos))})

(defn api-handler [request]
  {:status 200
   :body "This is the api handler"})

(defn wow-handler [request]
  {:status 200
   :body "This is the wow handler"})

(defn handler [request]
  (case (:uri request)
    "/api/" (api-handler request)
    "/api/todo/" (todo-handler request)
    "/wow/" (wow-handler request)
    {:status 404
     :body "not found"}))

(defn wrap-cors [handler]
  (fn [request]
    (let [response (handler request)]
      (-> response
       (assoc-in [:headers "Access-Control-Allow-Origin"] "*")
       (assoc-in [:headers "Access-Control-Allow-Methods"] "GET, POST, PUT, DELETE, PATCH, OPTIONS")
       (assoc-in [:headers "Access-Control-Allow-Headers"] "X-Requested-With, content-type, Authorization")))))

(defn server-start []
  (j/run-jetty (wrap-cors (var handler)) {:port 8000 :join? false}))

(comment

  (def server (server-start))
  (.stop server)
  (todo-handler [{}])

  (def test-request {:uri "/api/todo/"})
  (handler test-request))
