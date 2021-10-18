(defproject acds_net "0.1.0"
  :description "ACDS: network devices health status"

  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/java.jdbc "0.7.10"]
                 [cprop "0.1.14"]
                 [com.oracle.jdbc/ojdbc8 "12.2.0.1"]
                 [com.taoensso/timbre "4.10.0"]
                 [org.slf4j/slf4j-api "1.7.28"]
                 [com.fzakaria/slf4j-timbre "0.3.14"]
                 [hikari-cp "2.9.0"]
                 [commons-daemon/commons-daemon "1.2.2"]]

  :repositories [["XWiki External Repository" "https://maven.xwiki.org/externals/"]]

  :pedantic? false

  :jvm-opts ["-Dconf=config.edn"]

  :plugins [[lein-ancient "0.6.15"]]

  :min-lein-version "2.8.1"

  :source-paths ["src/clj"]

  :clean-targets ^{:protect false} [:target-path :compile-path]

  :main acds-net.core

  :profiles {:dev {:prep-tasks ["clean"]
                   :dependencies [[org.clojure/tools.nrepl "0.2.13"]
                                  [javax.servlet/servlet-api "2.5"]
                                  [proto-repl "0.3.1"]]}
             :uberjar {:uberjar-name "acds-net.jar"
                       :source-paths ^:replace ["src/clj"]
                       :prep-tasks ["compile"]
                       :hooks []
                       :omit-source true
                       :aot :all}})
