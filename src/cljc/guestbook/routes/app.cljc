(ns guestbook.routes.app
  (:require
   [spec-tools.data-spec :as ds]
   #?@(:clj [[guestbook.layout :as layout]
             [guestbook.middleware :as middleware]]
       :cljs [[guestbook.views.home :as home]
              [guestbook.views.profile :as profile]
              [guestbook.views.post :as post]
              [guestbook.views.author :as author]])))
#?(:clj
   (defn home-page [request]
     (layout/render
      request
      "home.html")))

(defn app-routes []
  [""
   #?(:clj {:middleware [middleware/wrap-csrf]
            :get home-page})
   ["/"
    (merge
     {:name ::home}
     #?(:cljs
        {:controllers home/home-controllers
         :parameters {:query {(ds/opt :post) pos-int?}}
         :view #'home/home}))]
   ["/post/:post"
    (merge
     {:name ::post}
     #?(:cljs {:controllers post/post-controllers
               :parameters {:path {:post pos-int?}}
               :view #'post/post-page}))]
   ["/my-account/edit-profile"
    (merge
     {:name ::profile}
     #?(:cljs
        {:controllers profile/profile-controllers
         :view #'profile/profile}))]
   ["/user/:user"
    (merge
     {:name ::author}
     #?(:cljs {:controllers author/author-controllers
               :parameters {:query {(ds/opt :post) pos-int?}
                            :path {:user string?}}
               :view #'author/author}))]])
