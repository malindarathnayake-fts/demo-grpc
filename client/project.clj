(defproject protojure-tutorial "0.0.1-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Apache License 2.0"
            :url "https://www.apache.org/licenses/LICENSE-2.0"
            :year 2021
            :key "apache-2.0"}
  :dependencies [[org.clojure/clojure "1.10.3"]

                 ;; logging
                 [com.taoensso/timbre "5.1.2"]
                 [com.fzakaria/slf4j-timbre "0.3.21"]

                 [ch.qos.logback/logback-classic "1.2.3" :exclusions [org.slf4j/slf4j-api]]
                 [org.slf4j/jul-to-slf4j "1.7.30"]
                 [org.slf4j/jcl-over-slf4j "1.7.30"]
                 [org.slf4j/log4j-over-slf4j "1.7.30"]

                 ;; -- PROTOC-GEN-CLOJURE --
                 [protojure "1.5.14"]
                 [protojure/google.protobuf "0.9.1"]
                 [com.google.protobuf/protobuf-java "3.13.0"]
                 ;; -- PROTOC-GEN-CLOJURE HTTP/2 Client Lib Dependencies --
                 [org.eclipse.jetty.http2/http2-client "9.4.20.v20190813"]
                 [org.eclipse.jetty/jetty-alpn-java-client "9.4.28.v20200408"]
                 ;; -- Jetty Client Dep --
                 [org.ow2.asm/asm "8.0.1"]]
  :source-paths ["src"]
  :resource-paths ["resources"]
  :aliases {"run-client" ["trampoline" "run" "-m" "grpccli.core/run-client"]}
  :main ^{:skip-aot true} grpccli.core)
