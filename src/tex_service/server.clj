(ns tex-service.server
  (:use ring.adapter.jetty))

(defn hello-app [req]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "Hello stupid world."})

(defn run-server []
  (run-jetty (var hello-app) {:port 8080 :join? false}))

