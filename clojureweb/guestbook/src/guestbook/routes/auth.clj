(ns guestbook.routes.auth
  (:require [compojure.core :refer [defroutes GET POST]]
            [guestbook.views.layout :as layout]
            [hiccup.form :refer
             [form-to label text-field password-field submit-button]]
            [noir.response :refer [redirect]]
            [noir.session :as session]
            [noir.validation :refer [rule errors? has-value? on-error]]
            [noir.util.crypt :as crypt]
            [guestbook.models.db :as db]))

(defn format-error [[error]]
  [:p.error error])

(defn control [field name text]
  (list (on-error name format-error)
        (label name text)
        (field name)
        [:br]))

(defn registration-page []
  (layout/common
    (form-to [:post, "/register"]
             (control text-field :id "Username")
             (control password-field :pass "Password")
             (control password-field :pass1 "Confirm Password")
             (submit-button "Create Account"))))

(defn login-page []
  (layout/common
    (form-to [:post "/login"]
             (control text-field :id "Username")
             (control password-field :pass "Password")
             (submit-button "Login"))))

(defn handle-registration [id pass pass1]
  (rule (= pass pass1)
        [:pass "Passwords do not match"])
  (if (errors? :pass)
    (registration-page)
    (do
      (db/add-user-record { :id id :pass (crypt/encrypt pass) })
      (redirect "/login"))))

(defn handle-login [id pass]
  (let [user (db/get-user id)]
    (rule (has-value? id)
          [:id "User name is required."])
    (rule (has-value? pass)
          [:pass "Password is required."])
    (rule (and user (crypt/compare pass (:pass user)))
          [:pass "Invalid password."])
    (if (errors? :id :pass)
      (login-page)
      (do
        (session/put! :user id)
        (redirect "/")))))

(defroutes auth-routes
  (GET  "/register" [_]             (registration-page))
  (POST "/register" [id pass pass1] (handle-registration id pass pass1))
  (GET "/login"     [_]             (login-page))
  (POST "/login"    [id pass]       (handle-login id pass))
  (GET "/logout"    [_]
       (layout/common
         (form-to [:post "/logout"]
                  (submit-button "logout"))))
  (POST "/logout" [_]
        (session/clear!)
        (redirect "/")))