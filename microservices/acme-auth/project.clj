(defproject acme-auth "0.1.0-SNAPSHOT"
  :description "Acme Corp Authentication Service"
  :url "https://github.com/rundis/acme-buddy/acme-auth"
  :min-lein-version "2.0.0"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/java.jdbc "0.3.6"]
                 [com.h2database/h2 "1.3.170"]
                 [hikari-cp "1.0.0"]
                 [compojure "1.2.1"]
                 [ring/ring-jetty-adapter "1.3.1"]
                 [ring/ring-json "0.3.1"]
                 [ch.qos.logback/logback-classic "1.1.2"]
                 [buddy/buddy-sign "0.3.0"]
                 [buddy/buddy-hashers "0.3.0"]]
  :ring {:handler acme-auth.core/app
         :port 6001
         :init acme-auth.core/bootstrap}
  :profiles {:dev {:plugins [[lein-ring "0.8.13"]]
                   :test-paths ^:replace []}
             :test {:dependencies [[midje "1.6.3"]]
                    :plugins [[lein-midje "3.1.3"]]
                    :test-paths ["test"]
                    :resource-paths ["test/resources"]}})
