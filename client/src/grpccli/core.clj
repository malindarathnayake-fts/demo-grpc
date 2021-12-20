(ns grpccli.core
  (:require [protojure.grpc.client.providers.http2 :as grpc.http2]
            [com.example.addressbook.Greeter.client :as greeter]
            [taoensso.timbre :as log]
            [taoensso.timbre.appenders.core :as appenders]
            [clojure.core.async :as async])
  (:gen-class))

(log/set-config! {:level :info
                  :appenders {:println (appenders/println-appender {:stream :auto})}})

(def grpc-url "http://localhost:8080")

(defn grpc-conn
  ([] (grpc-conn 1))
  ([attempt]
   (try
     (let [c (deref (grpc.http2/connect {:uri grpc-url}) 5000 nil)]
       (when (nil? c) (throw (Exception. "timeout getting client conn")))
       c)
     (catch Exception _
       (log/error (format "failed to obtaing a grpc client at [%s]" grpc-url))
       (Thread/sleep 5000)
       (grpc-conn (inc attempt))))))

(defn when-closed [future-to-watch callback]
  (future (callback
           (try
             @future-to-watch
             (catch Exception e
               (log/warn (format "subscription ended with message:%s cause:%s"
                                 (.getMessage e)
                                 (:message (ex-data (.getCause e))))))))))

(defn subscribe [client]
  (let [in (async/chan)
        out (async/chan)
        _ (log/info "subscribing to server")
        p (greeter/SayRepeatHello client in out)]
    (when-closed p #(when % (log/warn (format "subscription finished: %s" %))))
    [in out]))

(defn run-client []
  (log/info "demo client app started!")

  (let [client (grpc-conn)
        _ (log/info "acquired client with success")
        [in out] (subscribe client)]
    (async/go-loop []
      (when (.isClosed (:session (.context client)))
        (log/info "closed connection with server, exiting ...")
        (System/exit 0))
      (Thread/sleep 2000)
      (recur))
    (async/go-loop []
      (log/info "receiving data from server")
      (let [msg (async/<! out)]
        (log/info msg))
      (recur))
    (async/go-loop [n 0]
      (log/info "sending data to server")
      (async/>! in {:id n :name "Hey, this a message from client"})
      (Thread/sleep 5000)
      (recur (inc n)))))

(defn -main [& _]
  (run-client))