(ns picture-gallery.models.db
  (:require
            [clojure.java.jdbc :as sql]))
(def db
  {:subprotocol "postgresql"
   :subname     "//localhost/gallery"
   :user        "clojure"
   :password    "clojure"})

(defmacro with-db [f & body]
  `(sql/with-connection ~db (~f ~@body)))

(defn create-user [user]
  (sql/with-connection db
    (sql/insert-record :users user)))

(defn get-user [id]
  (sql/with-connection db
    (sql/with-query-results res ["SELECT * FROM users WHERE id = ?", id] (first res))))

(defn add-image [userid name]
  (with-db
    sql/transaction
    (if (sql/with-query-results res
          ["SELECT userid FROM images WHERE userid = ? and name = ?" userid name]
          (empty? res))
      (sql/insert-record :images {:userid userid :name name})
      (throw
        (Exception. "You have already uploaded an image with the same name")))))

(defn delete-image [userid name]
  (with-db
    sql/delete-rows :images ["userid=? and name=?" userid name]))

(defn images-by-user [userid]
  (with-db
    sql/with-query-results res
    ["SELECT * FROM images WHERE userid = ?", userid] (doall res)))

(defn get-gallery-previews []
  (with-db
    sql/with-query-results res
    ["SELECT * FROM
     (SELECT *, row_number() over (partition by userid) as row_number from images)
     as rows where row_number = 1"]
    (doall res)))

(defn delete-user [userid]
  (with-db
    sql/delete-rows :users ["id=?" userid]))
