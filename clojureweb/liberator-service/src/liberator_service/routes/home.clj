(ns liberator-service.routes.home
  (:require [compojure.core :refer :all]
            [liberator.core :refer [defresource resource request-method-in]]
            [cheshire.core  :refer [generate-string]]
            [noir.io :as io]
            [clojure.java.io :refer [file]]))

(def users (atom ["John" "Jane"]))

(defresource get-users
  :allowed-methods [:get]
  :handle-ok (fn[_] (generate-string @users))
  :available-media-types ["application/json"])

(defresource home
  :available-media-types ["text/html"]

  :exists?
  (fn [context]
    [(io/get-resource "/home.html")
     {::file (file (str (io/resource-path) "/home.html"))}])

  :handle-ok
  (fn [{{{ resource :resource } :route-params } :request }]
    (clojure.java.io/input-stream (io/get-resource "/home.html")))

  :last-modified
  (fn [{{{ resource :resource } :route-params } :request }]
    (.lastModified (file (str (io/resource-path) "/home.html")))))

(defresource add-user
  :allowed-methods [:post]
  :malformed?
  (fn [context]
    (let [params (get-in context [:request :form-params])]
      (empty? (get params "user"))))
  :handle-malformed "User name can't be empty!"
  :post!
  (fn [context]
    (let [params (get-in context [:request :form-params])]
      (swap! users conj (get params "user"))))
  :handle-created (fn [_] (generate-string @users))
  :available-media-types ["application/json"])

(defroutes home-routes
  (ANY "/" request home)
  (GET "/users" request get-users )
  (POST "/add-user" request add-user))
