(ns user (:require [ring.middleware.resource :refer [wrap-resource]]))

(def app (wrap-resource identity "public"))
