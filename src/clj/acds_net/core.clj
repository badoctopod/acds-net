(ns acds-net.core
  (:require
    [clojure.java.jdbc :as jdbc]
    [hikari-cp.core :refer [make-datasource close-datasource]]
    [cprop.core :refer [load-config]]
    [taoensso.timbre :as timbre]
    [taoensso.timbre.appenders.3rd-party.rolling :as appender])
  (:import
    [org.apache.commons.daemon Daemon DaemonContext])
  (:gen-class
   :implements [org.apache.commons.daemon.Daemon]
   :methods [^{:static true} [start ["[Ljava.lang.String;"] void]
             ^{:static true} [stop ["[Ljava.lang.String;"] void]]))

(def config (load-config))

(def ds-utl (delay (try
                     (make-datasource (:utl config))
                     (catch Exception _ nil))))

(defn reachable?
  [host timeout]
  (try
    (.isReachable (java.net.InetAddress/getByName host) timeout)
    (catch Exception _ false)))

(defn get-devices
  []
  (try
    (jdbc/with-db-connection [conn {:datasource @ds-utl}]
      (jdbc/query conn ["SELECT device_id, device_ip
                         FROM   acds.net_devices"]))
    (catch Exception _ nil)))

(defn get-statuses
  [devices timeout]
  (if-not (nil? devices)
    (for [d devices :let [id (:device_id d)
                          host (:device_ip d)]]
      {:id id :status (if (reachable? host timeout) "Live" "Dead")})
    nil))

(defn update-devices!
  [timeout]
  (let [devices (get-devices)
        statuses (get-statuses devices timeout)]
    (if-not (nil? statuses)
      (try
        (jdbc/with-db-transaction [conn {:datasource @ds-utl}]
          (doseq [s statuses
                  :let [id (:id s)
                        status (:status s)]]
            (jdbc/execute! conn ["UPDATE acds.net_devices
                                  SET    device_status = ?, last_updated = sysdate
                                  WHERE  device_id = ?"
                                 status id])))
        (catch Exception e
             (timbre/errorf (str (.getMessage e))))))))

(def job
  (delay
    (future
      (while (not (Thread/interrupted))
        (timbre/infof "Starting ICMP echo thread...")
        (while true
          (do (Thread/sleep (:period (:ping config)))
              (timbre/infof "Updating network devices health status...")
              (update-devices! (:timeout (:ping config)))
              (timbre/infof "Update completed")))))))

(defn start
  []
  (timbre/merge-config!
    {:level (:level (:log config))
     :timestamp-opts {:pattern (:timestamp (:log config))}
     :appenders {:rolling (appender/rolling-appender
                            {:path (:path (:log config))
                             :pattern (:rotation (:log config))})}})
  (timbre/infof "Starting main thread...")
  @ds-utl
  @job)

(defn stop
  []
  (future-cancel @job)
  (try
    (close-datasource @ds-utl)
    (catch Exception _
     (timbre/warnf "UTL db connection pool hasn't been started, nothing to stop")))
  (shutdown-agents)
  (timbre/infof "ICMP echo thread stopped")
  (timbre/infof "Main thread stopped"))

(defn -start
  [this]
  (start))

(defn -stop
  [this]
  (stop))

(defn -main
  [& args]
  (start))
