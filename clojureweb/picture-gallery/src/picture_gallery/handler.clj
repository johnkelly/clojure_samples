(ns picture-gallery.handler
  (:require [compojure.core :refer [defroutes]]
            [compojure.route :as route]
            [noir.util.middleware :as noir-middleware]
            [noir.session :as session]
            [picture-gallery.routes.home    :refer [home-routes]]
            [picture-gallery.routes.upload  :refer [upload-routes]]
            [picture-gallery.routes.gallery :refer [gallery-routes]]
            [picture-gallery.routes.auth    :refer [auth-routes]]))

(defn init []
  (println "picture-gallery is starting"))

(defn destroy []
  (println "picture-gallery is shutting down"))

(defn user-page [_]
  (session/get :user))

(defroutes app-routes
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (noir-middleware/app-handler
    [auth-routes home-routes upload-routes gallery-routes app-routes]
    :access-rules [user-page]))
