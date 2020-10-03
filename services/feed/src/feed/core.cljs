(ns feed.core
  (:require [taoensso.timbre :as timbre]
            [reitit.ring :as ring]
            [feed.data :as data]
            [environ.core :refer [env]]
            [macchiato.middleware.restful-format :as rf]
            [feed.middleware :as middleware]
            [macchiato.server :as http]))


(defn premium-feed [request respond _]
  (fn [request respond _] )
  (respond
   {:status 200
    :body   data/premium-feed}))

(defn patriota-feed [request respond _]
  (fn [request respond _] )
  (respond
   {:status 200
    :body   data/patriota-feed}))

(def app-routes
  ["/feed" 
   ["/patriota" {:get {:middleware [(middleware/require-scope "patriota")]
                       :handler    patriota-feed}}]
   ["/premium" {:get {:middleware [(middleware/require-scope "premium")]
                      :handler    premium-feed}}]])

(defn make-handler [routes middleware]
  (ring/ring-handler
   (ring/router [routes] {:data {:middleware middleware}})
   (ring/create-default-handler)))

(defn main []
  (let [host         (env :hostname "127.0.0.1")
        port         (env :port 3000)
        userinfo-url (env :userinfo-url )
        middleware   [middleware/capture-auth-token
                      (middleware/get-scopes userinfo-url)
                      middleware/capture-exceptions]]
    (when (not userinfo-url)
      (timbre/error "Missing USERINFO_URL")
      (js/process.exit))
    (http/start
     {:handler    (make-handler app-routes middleware)
      :host       host
      :port       port
      :on-success #(timbre/info "Server started on" host ":" port)})))
