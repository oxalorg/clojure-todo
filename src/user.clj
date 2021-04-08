(ns user (:require [ring.middleware.resource :refer [wrap-resource]]))

(defn wrap-cors [handler]
  (fn [request]
    (let [response (handler request)]
      (assoc-in response [:headers "Access-Control-Allow-Origin"] "*"))))

(def app
  (wrap-cors
   (wrap-resource identity "public")))
