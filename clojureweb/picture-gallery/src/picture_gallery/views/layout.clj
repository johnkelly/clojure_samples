(ns picture-gallery.views.layout
  (:require
    [hiccup.page :refer [html5 include-css include-js]]
    [hiccup.element :refer [link-to]]
    [noir.session :as session]
    [hiccup.form :refer :all]
    [ring.util.response :refer [content-type response]]
    [compojure.response :refer [Renderable]]))

(defn utf-8-response [html]
  (content-type (response html) "text/html; charset=utf-8"))

(deftype RenderablePage [body]
  Renderable
  (render [this request]
    (utf-8-response
      (html5
        [:head
         [:title "Welcome to picture-gallery"]
         (include-css "/css/screen.css")
         [:script {:type "text/javascript"}
          (str "var context=\"" (:context request) "\";")]
         (include-js "//code.jquery.com/jquery-2.0.2.min.js")]
        [:body body]))))

(defn base [& body]
  (RenderablePage. body))

(defn make-menu [& items]
  [:div (for [item items] [:div.menutitem item])])

(defn guest-menu []
  (make-menu
    (link-to "/" "home")
    (link-to "/register" "Register")
    (form-to [:post "/login"]
             (text-field {:placeholder "User ID"} "id")
             (password-field {:placeholder "Password"} "pass")
             (submit-button "Login"))))

(defn user-menu [user]
  (make-menu
    (link-to "/" "Home")
    (link-to "/upload" "Upload Images")
    (link-to "/logout" (str "logout " user))
    (link-to "/delete-account" "Delete Account")))

(defn common [& body]
  (base
    (if-let [user (session/get :user)]
      (user-menu user)
      (guest-menu))
    [:div.content body]))
