(ns grpcsrv.server
  (:gen-class) ; for -main method in uberjar
  (:require
   [clojure.core.async :as async]
   [io.pedestal.http :as server]
   [io.pedestal.http.route :as route]
   [grpcsrv.service :as service]
   [taoensso.timbre :as log]
   [taoensso.timbre.appenders.core :as appenders]))

(log/set-config! {:level :info
                  :appenders {:println (appenders/println-appender {:stream :auto})}})

;; This is an adapted service map, that can be started and stopped
;; From the REPL you can call server/start and server/stop on this service

(defonce runnable-service (server/create-server service/service))

(defn run-server
  "The entry-point for 'lein run-server'"
  [& _]
  (log/info "Starting demo server")
  (async/go-loop []
    (log/info "control loop write channel ...")
    (let [chan @service/write-chan]
      (when (some? chan)
        (log/info "sending message to client ...")
        (async/>! chan {:message (str "Hey, this is a message from the server")}))
      (Thread/sleep 5000)
      (recur)))
  (async/go-loop []
    (log/info "control loop read channel ...")
    (let [chan @service/read-chan
          msg (when (some? chan) (async/<! chan))]
      (when (some? msg)
        (log/info msg))
      (Thread/sleep 5000)
      (recur)))
  (-> service/service ;; start with production configuration
      (merge {:env :dev
              ;; do not block thread that starts web server
              ::server/join? false
              ;; Routes can be a function that resolve routes,
              ;;  we can use this to set the routes to be reloadable
              ::server/routes #(route/expand-routes (deref #'service/grpc-routes)) ;; -- PROTOC-GEN-CLOJURE -- update route
              ;; all origins are allowed in dev mode
              ::server/allowed-origins {:creds true :allowed-origins (constantly true)}
              ;; Content Security Policy (CSP) is mostly turned off in dev mode
              ::server/secure-headers {:content-security-policy-settings {:object-src "none"}}})
      ;; Wire up interceptor chains
      server/default-interceptors
      server/dev-interceptors
      server/create-server
      server/start))

(defn -main
  "The entry-point for 'lein run'"
  [& args]
  (println "\nCreating your server...")
  (server/start runnable-service))

;; If you package the service up as a WAR,
;; some form of the following function sections is required (for io.pedestal.servlet.ClojureVarServlet).

;;(defonce servlet  (atom nil))
;;
;;(defn servlet-init
;;  [_ config]
;;  ;; Initialize your app here.
;;  (reset! servlet  (server/servlet-init service/service nil)))
;;
;;(defn servlet-service
;;  [_ request response]
;;  (server/servlet-service @servlet request response))
;;
;;(defn servlet-destroy
;;  [_]
;;  (server/servlet-destroy @servlet)
;;  (reset! servlet nil))

