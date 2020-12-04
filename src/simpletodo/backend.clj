(ns simpletodo.backend
  (:require [ring.adapter.jetty :as j]))


(defn api-handler [request]
  {:status 200
   :body "This is the api handler"})

(defn wow-handler [request]
  {:status 200
   :body "This is the wow handler"})

(defn handler [request]
  (case (:uri request)
    "/api/" (api-handler request)
    "/wow/" (wow-handler request)
    {:status 404
     :body "not found"}))

(def server 
  (j/run-jetty (var handler) {:port 8000 :join? false}))