# Compojure-api [![Build Status](https://travis-ci.org/metosin/compojure-api.png)](https://travis-ci.org/metosin/compojure-api) [![Dependencies Status](http://jarkeeper.com/metosin/compojure-api/status.png)](http://jarkeeper.com/metosin/compojure-api)

Stuff on top of [Compojure](https://github.com/weavejester/compojure) for making sweet web apis.

- [Schema](https://github.com/Prismatic/schema) for input & output data coercion
- [Swagger](https://github.com/wordnik/swagger-core/wiki) for api documentation, via [ring-swagger](https://github.com/metosin/ring-swagger)
- extendable request restructuring via metadata handlers ([woot?](#creating-your-own-metadata-handlers))
- bundled middleware for common api behavior (exception mapping, data formats & serialization)
- route macros for putting things together, including the [Swagger-UI](https://github.com/wordnik/swagger-ui)
   - uses the `ring-swagger-ui` which is needed as separate dependency (separate lifecycle)

## Latest version

[![Clojars Project](http://clojars.org/metosin/compojure-api/latest-version.svg)](http://clojars.org/metosin/compojure-api)

## Sample application

```clojure
(ns examples.thingie
  (:require [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [schema.core :as s]))

;;
;; Schemas
;;

(s/defschema Thingie {:id Long
                      :hot Boolean
                      :tag (s/enum :kikka :kukka)
                      :chief [{:name String
                               :type #{{:id String}}}]})

;;
;; Routes
;;

(defroutes* legacy-route
  (GET* "/legacy/:value" [value]
    (ok {:value value})))

(defapi app
  (swagger-ui)
  (swagger-docs
    :title "Sample api")
  (swaggered "thingie"
    :description "There be thingies"
    (context "/api" []

      (GET* "/plus" []
        :return       Long
        :query-params [x :- Long {y :- Long 1}]
        :summary      "x+y with query-parameters. y defaults to 1."
        (ok (+ x y)))

      (POST* "/minus" []
        :return      Long
        :body-params [x :- Long y :- Long]
        :summary     "x-y with body-parameters."
        (ok (- x y)))

      (GET* "/times/:x/:y" []
        :return      Long
        :path-params [x :- Long y :- Long]
        :summary     "x*y with path-parameters"
        (ok (* x y)))

      (POST* "/divide" []
        :return      Double
        :form-params [x :- Long y :- Long]
        :summary     "x/y with form-parameters"
        (ok (/ x y)))

      (GET* "/power" []
        :return      Long
        :header-params [x :- Long y :- Long]
        :summary     "x^y with header-parameters"
        (ok (long (Math/pow x y))))

      legacy-route

      (PUT* "/echo" []
        :return   [{:hot Boolean}]
        :body     [body [{:hot Boolean}]]
        :summary  "echoes a vector of anonymous hotties"
        (ok body))

      (POST* "/echo" []
        :return   Thingie
        :body     [thingie Thingie]
        :summary  "echoes a Thingie from json-body"
        (ok thingie)))))
```

To try it yourself, clone this repo and type
- `lein start-thingie` (Jetty)
- `lein http-kit-thingie` (Http-kit)

## Quickstart for a new project

Clone the [examples-repo](https://github.com/metosin/compojure-api-examples).

Use a Leiningen template, with or without tests:

```
lein new compojure-api my-api
lein new compojure-api my-api +midje
lein new compojure-api my-api +clojure-test
```

## Current (major) issues

* `defroutes*` does not handle swagger-meta-data model namespace resolution correctly. One has to import all used namespaces to root api namespace. See https://github.com/metosin/compojure-api/issues/42. `defroutes*` will be rewritten for Swagger 2.0.
   - currently best way is to push all api models into separate namespace.

# Building Documented Apis

## Middlewares

There is pre-packaged middleware `compojure.api.middleware/api-middleware` for common web api usage. It's a enchanced version of `compojure.handler/api` adding the following:

- catching slinghotted http-errors (`ring.middleware.http-response/catch-response`)
- catching model validation errors (`ring.swagger.middleware/catch-validation-errors`)
- support for different protocols via `ring.middleware.format-params/wrap-restful-params` and `ring.middleware.format-response/wrap-restful-response`
    - default supported protocols are:
       - `:json-kw`, `:yaml-kw`, `:edn`, `:transit-json` and `:transit-msgpack`

### Mounting middlewares

To help setting up custom middleware there is a `middlewares` macro:

```clojure
(ns example
  (:require [ring.util.http-response :refer [ok]]
            [compojure.api.middleware :refer [api-middleware]]
            [compojure.api.core :refer [middlewares]]
            [compojure.core :refer :all]))

(defroutes app
  (middlewares [api-middleware]
    (context "/api" []
      (GET "/ping" [] (ok {:ping "pong"})))))
```

There is also `defapi` as a short form for the common case of defining routes with `api-middleware`:

```clojure
(ns example2
  (:require [ring.util.http-response :refer [ok]]
            [compojure.api.core :refer [defapi]]
            [compojure.core :refer :all]))

(defapi app
  (context "/api" []
    (GET "/ping" [] (ok {:ping "pong"}))))
```

## Request & response formats

Middlewares (and other handlers) can publish their capabilities to consume & produce different wire-formats. This information is passed to `ring-swagger` and added to swagger-docs & is available in the swagger-ui.

The default middlewares on Compojure-API includes [ring-middleware-format](https://github.com/ngrunwald/ring-middleware-format) which supports multiple formats. If the first element of `defapi` body is a map it will be used to pass parameters to `api-middleware`, e.g. the formats which should be enabled.

```clojure
(defapi app
  {:formats [:transit-json :edn]}
  (context "/api" [] ...))
```

One can add own format middlewares (XML etc.) and add expose their capabilities by adding the
supported content-type into request under keys `[:meta :consumes]` and `[:meta :produces]` accordingly.

## Routes

You can use [vanilla Compojure routes](https://github.com/weavejester/compojure/wiki) or their enchanced versions from `compojure.api.core`. Enchanced versions have `*` in their name (`GET*`, `POST*`, `defroutes*` etc.) so that they don't get mixed up with the originals. Enchanced version can be used exactly as their ancestors but have also new behavior, more on that later.

Namespace `compojure.api.sweet` is a public entry point for all routing - importing Vars from `compojure.api.core`, `compojure.api.swagger` and `compojure.core`.

There is also `compojure.api.legacy` namespace which contains rest of the public vars from `compojure.core` (the `GET`, `POST` etc. endpoint macros which are not contained in `sweet`). Using `sweet` in conjuction with `legacy` should provide a drop-in-replacement for `compojure.core` - with new new route goodies.

### sample sweet application

```clojure
(ns example3
  (:require [ring.util.http-response :refer [ok]]
            [compojure.api.sweet :refer :all]))

(defapi app
  (context "/api" []
    (GET* "/user/:id" [id] (ok {:id id}))
    (POST* "/echo" {body :body-params} (ok body))))
```

## Route documentation

Compojure-api uses [Swagger](https://github.com/wordnik/swagger-core/wiki) for route documentation.

Enabling Swagger route documentation in your application is done by:

- wrapping your web app in a `compojure.api.core/defapi` (or `compojure.api.routes/with-routes`) macro. This initializes an empty route tree to your namespace.
- wrapping your web apis in a `swaggered` -route macro on to the root level of your web app.
  - uses macro-peeling & source linking to reconstruct the route tree from route macros at macro-expansion time (~no runtime penanty)
  - if you intend to split your routes behind multiple Vars via `defroutes`, use `defroutes*` instead so that their routes get also collected.
- mounting `compojure.api.swagger/swagger-docs` to publish the collected routes.
- **optionally** mounting `compojure.api.swagger/swagger-ui` to add the [Swagger-UI](https://github.com/wordnik/swagger-ui) to the web app
  - the ui is packaged separately at clojars with name `metosin/metosin/ring-swagger-ui`

Currently, there can be only one `defapi` or `with-routes` per namespace.

There can be several `swaggered` apis in one web application.

### sample minimalistic swaggered app

```clojure
(ns example4
  (:require [ring.util.http-response :refer [ok]]
            [compojure.api.sweet :refer :all]))

(defroutes* legacy-route
  (GET* "/ping/:id" [id]
    (ok {:id id})))

(defapi app
  (swagger-ui)
  (swagger-docs)
  (swaggered "test"
    :description "Swagger test api"
    (context "/api" []
      legacy-route
      (POST* "/echo" {body :body-params} (ok body)))))
```

By default, Swagger-UI is mounted to the root `/` and api-listing to `/api/api-docs`.

Most route functions & macros have a loose (DSL) syntax taking optional parameters and having an easy way to add meta-data.

```clojure
  ; with defaults
  (swagger-docs)

  ; all said
  (swagger-docs "/api/api-docs"
    :title "Cool api"
    :apiVersion "1.0.0"
    :description "Compojure Sample Web Api"
    :termsOfServiceUrl "http://www.metosin.fi"
    :contact "pizza@example.com"
    :license "Eclipse 1.0"
    :licenseUrl "http://www.eclipse.org/legal/epl-v10.html")
```

See source code & [examples](https://github.com/metosin/compojure-api/blob/master/src/compojure/api/example/handler.clj#) for more details.

## Models

Compojure-api uses the [Schema](https://github.com/Prismatic/schema)-based modelling, backed up by [ring-swagger](https://github.com/metosin/ring-swagger) for mapping the models int Swagger/JSON Schemas.

Two coercers are available (and automatically selected with smart destucturing): one for json and another for string-based formats (query-parameters & path-parameters). See [Ring-Swagger](https://github.com/metosin/ring-swagger#schema-coersion) for more details.

### sample schema and coercion

```clojure
(require '[ring.swagger.schema :as ss])
(require '[schema.core :as s])

(s/defschema Thingie {:id Long
                      :tag (s/enum :kikka :kukka)})

(ss/coerce! Thingie {:id 123
                     :tag "kikka"})
; => {:id 123 :tag :kikka}

(ss/coerce! Thingie {:id 123
                     :tags "kakka"})
; => ExceptionInfo throw+: {:type :ring.swagger.schema/validation, :error {:tags disallowed-key, :tag missing-required-key}}  ring.swagger.schema/coerce! (schema.clj:88)
```

## Models, routes and meta-data

The enchanced route-macros allow you to define extra meta-data by adding a) meta-data as a map or b) as pair of keyword-values in Liberator-style. With meta-data you can set both input and return models and some Swagger-specific data like nickname and summary. Input models have smart schema-aware destructuring and do automatic data coersion.

```clojure
  (POST* "/echo" []
    :return   FlatThingie
    :query    [flat-thingie FlatThingie]
    :summary  "echos a FlatThingie from query-params"
    :nickname "echoFlatThingiePost"
    (ok flat-thingie)) ;; here be coerced thingie
```

```clojure
  (GET* "/echo" []
    :return   Thingie
    :query    [thingie Thingie]
    :summary  "echos a thingie from query-params"
    :nickname "echoThingieQuery"
    (ok thingie)) ;; here be coerced thingie
```

you can also wrap models in containers (`vector` and `set`) and add extra metadata:

```clojure
  (POST* "/echos" []
    :return   [Thingie]
    :body     [thingies (describe #{Thingie} "set on thingies")]
    (ok thingies))
```

From `0.12.0` on, anonoymous schemas are also supported:

```clojure
  (PUT* "/echos" []
    :return   [{:id Long, :name String}]
    :body     [body #{{:id Long, :name String}}]
    (ok body))
```

## Query, Path, Header and Body parameters

All parameters can also be destructured using the [Plumbing](https://github.com/Prismatic/plumbing) syntax with optional type-annotations:

```clojure
(GET* "/sum" []
  :query-params [x :- Long, y :- Long]
  (ok {:total (+ x y)}))

(GET* "/times/:x/:y" []
  :path-params [x :- Long, y :- Long]
  (ok {:total (* x y)}))

(POST* "/divide" []
  :return Double
  :form-params [x :- Long, y :- Long]
  (ok {:total (/ x y)}))

(POST* "/minus" []
  :body-params [x :- Long, y :- Long]
  (ok {:total (- x y)}))

(POST* "/power" []
  :header-params [x :- Long, y :- Long]
  (ok {:total (long (Math/pow x y))})
```

## Returning raw values

Raw values / primitives (e.g. not sequences or maps) can be returned when a `:return` -metadata is set. Swagger, [ECMA-404](http://www.ecma-international.org/publications/files/ECMA-ST/ECMA-404.pdf) and ECMA-262 allow this (while RFC4627 forbids it).

*note* setting a `:return` value as `String` allows you to return raw strings (as JSON or whatever protocols your app supports), opposed to the [Ring Spec](https://github.com/mmcgrana/ring/blob/master/SPEC#L107-L120).

```clojure
(context "/primitives" []

  (GET* "/plus" []
    :return       Long
    :query-params [x :- Long {y :- Long 1}]
    :summary      "x+y with query-parameters. y defaults to 1."
    (ok (+ x y)))

  (GET* "/datetime-now" []
    :return org.joda.time.DateTime
    :summary "current datetime"
    (ok (org.joda.time.DateTime.)))

  (GET* "/hello" []
    :return String
    :query-params [name :- String]
    :notes   "<h1>hello world.</h1>"
    :summary "echos a string from query-params"
    (ok (str "hello, " name))))
```

## Response-models

Key `:responses` takes a map of http-status-code -> model map, which translates to both return model coercion and to swagger `responseMessages` description. Models can be decorated with `:message` meta-data.

```clojure
(POST* "/number" []
  :return       Long
  :query-params [x :- Long y :- Long]
  :responses    {403 ^{:message "Underflow"} ErrorEnvelope}
  :summary      "x-y with body-parameters."
  (let [total (- x y)]
    (if (pos? total)
      (ok total)
      (forbidden {:message "difference is negative"}))))
```

## Route-specific middlewares

Key `:middlewares` takes a vector of middlewares to be applied to the route.
Note that the middlewares don't see any restructured bindinds from within the route body.
They are executed inside the route so you can safely edit request etc. and the changes
won't leak to other routes in the same context.

```clojure
 (DELETE* "/user/:id" []
   :middlewares [audit-support (for-roles :admin)]
   (ok {:name "Pertti"})))
```

## Creating your own metadata handlers

Compojure-api handles the route metadatas by calling the multimethod `compojure.api.meta/restructure-param` with metadata key as a dispatch value.

Multimethods take three parameters:

1. metadata key
2. metadata value
3. accumulator map with keys
    - `:lets`, a vector of let bindings applied first before the actual body
    - `:letks`, a vector of letk bindings applied second before the actual body
    - `:middlewares`, a vector of route specific middlewares (applied from left to right)
    - `:parameters`, meta-data of a route (without the key & value for the current multimethod)
    - `:body`, a sequence of the actual route body

.. and should return the modified accumulator. Multimethod calls are reduced to produce the final accumulator for code generation. Defined key-value -based metadatas for routes are guaranteed to run on top-to-bottom order of the so all the potential `let` and `letk` variable overrides can be solved by the client. Default implementation is to keep the key & value as a route metadata.

You can add your own metadata-handlers by implementing the multimethod:

```clojure
(defmethod compojure.api.meta/restructure-param :auth
  [_ token {:keys [parameters lets body middlewares] :as acc}]
  "Make sure the request has X-AccessToken header and that it's value is 123. Binds the value into a variable"
  (-> acc
      (update-in [:lets] into [{{token "x-accesstoken"} :headers} '+compojure-api-request+])
      (assoc :body `((if (= ~token "123")
                      (do ~@body)
                      (ring.util.http-response/forbidden "Auth required"))))))
```

using it:

```clojure
(GET* "/current-session" []
  :auth token
  (ok {:token token}))
 ```

macroexpanding-1 it too see what's get generated:

```clojure
(clojure.pprint/pprint
    (macroexpand-1 `(GET* "/current-session" []
                          :auth token
                          (ok {:token token}))))

(compojure.core/GET
 "/current-session"
 [:as +compojure-api-request+]
 (clojure.core/let
  [{{examples.thingie/token "x-accesstoken"} :headers}
   +compojure-api-request+]
  (do
   (if
    (clojure.core/= examples.thingie/token "123")
    (do (ring.util.http-response/ok {:token examples.thingie/token}))
    (ring.util.http-response/forbidden "Auth required")))))
```

## Running the embedded example(s)

`lein start-samples`

## Features and weird things

- All routes are collected at compile-time
  - there is basically no runtime penalty for describing your apis
  - all runtime code between route-macros are ignored when macro-peeling route-trees. See [tests](https://github.com/metosin/compojure-api/blob/master/test/compojure/api/swagger_test.clj)
  - `swaggered` peels the macros until it reaches `compojure.core` Vars. You can write your own DSL-macros on top of those

## Roadmap

- don't pollute api namespases with `+routes+` var, use lexically/dynamically scoped route tree instead
- collect routes from root, not from `swaggered`
- type-safe `:params` destructuring
- `url-for` for endpoints (bidi, bidi, bidi)
- include external common use middlewares (ring-middleware-format, ring-cors etc.)
- file handling
- `WS*`?

## License

Copyright © 2014-2015 [Metosin Oy](http://www.metosin.fi)

Distributed under the Eclipse Public License, the same as Clojure.
