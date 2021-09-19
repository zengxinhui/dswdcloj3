(ns guestbook.core
  (:require [reagent.core :as r]
            [reagent.dom :as dom]
            [clojure.string :as string]
            [ajax.core :refer [GET POST]]))

(defn send-message! [fields errors]
  (POST "/message"
        {:params @fields
         :format :json
         :headers
         {"Accept" "application/transit+json"
          "x-csrf-token" (.-value (.getElementById js/document "token"))}
         :handler (fn [r]
                    (.log js/console (str "response:" r))
                    (reset! errors nil))
         :error-handler (fn [e]
                          (.log js/console (str e))
                          (reset! errors (-> e :response :errors)))}))

(defn errors-component [errors id]
  (when-let [error (id @errors)]
    [:div.notification.is-danger (string/join error)]))

(defn message-form []
  (let [fields (r/atom {})
        errors (r/atom nil)]
    (fn []
      [:div
       [:p "Name: " (:name @fields)]
       [:p "Message: " (:message @fields)]
       [errors-component errors :server-error]
       [:div.field
        [:label.label {:for :name} "Name"]
        [errors-component errors :name]
        [:input.input
         {:type :text
          :name :name
          :on-change #(swap! fields
                             assoc :name (-> % .-target .-value))
          :value (:name @fields)}]]
       [:div.field
        [:label.label {:for :message} "Message"]
        [errors-component errors :message]
        [:textarea.textarea
         {:name :message
          :value (:message @fields)
          :on-change #(swap! fields
                             assoc :message (-> % .-target .-value))}]]
       [:input.button.is-primary
        {:type :submit
         :on-click #(send-message! fields errors)
         :value "comment"}]])))

(defn home []
  [:div.content>div.columns.is-centered>div.column.is-two-thirds
   [:div.columns>div.column
    [message-form]]])

(dom/render
 [home]
 (.getElementById js/document "content"))
