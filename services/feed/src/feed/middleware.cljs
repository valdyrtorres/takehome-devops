(ns feed.middleware
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [clojure.string :as str]
            [cljs-http.client :as http]
            ["xhr2" :as xhr2]
            [cljs.core.async :refer [<!]]))


;; See https://github.com/r0man/cljs-http/issues/94
(set! js/XMLHttpRequest xhr2)

(defn capture-exceptions
  [handler]
  (fn [request respond _]
    (try
      (handler request respond _)
      (catch :default e
        (let [exception-type (:type (.-data e))]
          (cond
            (= exception-type :errors/missing-authorization-token)
            (respond {:status 400
                      :body   {:message "Missing authorization token"}})

            (= exception-type :errors/unauthorized)
            (respond {:status 403
                      :body   {:message "Unauthorized"}})
            :else
            (respond {:status 500
                      :body   {:message "Internal server error"}})))))))

(defn capture-auth-token
  [handler]
  (fn [request respond _]
    (if-let [auth-token (some-> request :headers (get "authorization") (str/split #" ") second)]
      (handler (assoc request :auth-token auth-token) respond _)
      (throw (ex-info "missing-authorization-token" {:type :errors/missing-authorization-token})))))

(defn- request-token [url auth-token]
  (http/get url
            {:with-credentials? false
             :headers           {"Authorization" (str "Bearer " auth-token)}}))

(defn get-scopes [userinfo-endpoint]
  (fn [handler]
    (fn [request respond _]
      (go (let [response (<! (request-token userinfo-endpoint (:auth-token request)))
                scopes   (-> (get-in response [:body :scopes])
                             (str/split #" ") set)]
            (handler (assoc request :scopes scopes) respond _))))))

(defn require-scope [scope]
  (fn [handler]
    (fn [request respond _]
      (if (get (:scopes request) scope)
        (handler request respond _)
        (throw (ex-info "unauthorized" {:type :errors/unauthorized}))))))
