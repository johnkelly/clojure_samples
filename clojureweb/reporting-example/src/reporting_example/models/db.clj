(ns reporting-example.models.db
  (:require [clojure.java.jdbc :as sql]))

(def db {:subprotocol "postgresql"
         :subname     "//localhost/reporting"
         :user        "clojure"
         :password    "clojure"})

(defn create-employee-table []
  (sql/create-table :employee
                    [:name       "varchar(50)"]
                    [:occupation "varchar(50)"]
                    [:place      "varchar(50)"]
                    [:country    "varchar(50)"]))

(defn create-employee-seed []
  (sql/insert-rows :employee
                   ["Albert Einstein"    "Engineer"         "Ulm"     "Germany"]
                   ["Albert Hitchcock"   "Movie Director"   "London"  "UK"]
                   ["Wernher Von Braun"  "Rocket Scientist" "Wyrzysk" "Poland"]
                   ["Sigmund Freud"      "Neurologist"      "Pribor"  "Czech Republic"]
                   ["Mahatma Gandhi"     "Lawyer"           "Gujarat" "India"]
                   ["Sachin Tendulkar"   "Cricket Player"   "Mumbai"  "India"]
                   ["Michael Schumacher" "F1 Racer"         "Cologne" "Germany"]))

(defn read-employees []
  (sql/with-connection db
    (sql/with-query-results rs ["SELECT * FROM employee"] (doall rs))))

(sql/with-connection db
  (create-employee-table)
  (create-employee-seed))
