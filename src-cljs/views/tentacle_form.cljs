(ns doctopus.views.tentacle-form
  (:require [cljs.core.async :refer [<!]]
            [cljs-http.client :as http]
            [doctopus.util :refer [get-value http-ok? redirect-to]]
            [doctopus.views.common :refer [button]]
            [reagent.core :refer [atom]]
            [doctopus.validation :as validation])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(enable-console-print!)

(defonce form-data (atom {}))
(defonce errors (atom [:name-empty :source-empty :command-empty]))
(defonce csrf-token (atom ""))

(defn- submit-form
  [submit-url]
  (let [data @form-data]
    (go
      (let [response (<! (http/post submit-url
                                    {:json-params data
                                     :headers {"X-CSRF-Token" @csrf-token}}))]
        (if (http-ok? (:status response))
          (redirect-to (get-in response [:body :success-url])))))))

(defn- head-selector
  [heads]
  [:select#head-selector
    (for [head heads]
      (let [head-name (:name head)]
        ^{:key head-name} [:option {:value head-name} head-name]))])

(defn- input
  [id initial-value on-change]
  [(keyword (str "input#" id)) {:type "text"
                                :value initial-value
                                :on-change on-change}])

(defn- tentacle-input
  []
  (input "tentacle-name" (:name @form-data)
         #(swap! form-data assoc :name (get-value %))))

(defn- tentacle-source-input
  []
  (input "tentacle-source" (:source @form-data)
         #(swap! form-data assoc :source (get-value %))))

(defn- tentacle-command-input
  []
  [:textarea#tentacle-command
   {:on-change #(swap! form-data assoc :command (get-value %))}])

(defn tentacle-form
  [{:keys [csrf submit-url original-name heads tentacles] :or {original-name ""}}]
  (let [validation-map {:name    [[(partial validation/field-duplicate-value? :name tentacles) :name-taken]
                                  [validation/field-invalid-characters? :name-invalid]
                                  [validation/field-empty? :name-empty]]
                        :source  [[validation/field-empty? :source-empty]]
                        :command [[validation/field-empty? :command-empty]]}]
    (do
      (swap! form-data assoc :original-name original-name :name original-name)
      (reset! csrf-token csrf)
      (add-watch form-data :validation
                 (validation/create-form-validator validation-map #(reset! errors %)))
      (fn []
        [:form.main
         [:fieldset
          [:div
           [:label {:for "head-selector"} "Belonging to:"]
           [head-selector heads]]
          [:div
           [:label {:for "tentacle-name"} "Tentacle Name"]
           [tentacle-input]]
          [:div
           [:label {:for "tentacle-source"} "Tentacle Source"]
           [tentacle-source-input]]
          [:div
           [:label {:for "tentacle-command"} "Tentacle Command"]
           [tentacle-command-input]]]
         (if (> (count @errors) 0)
           [button #(submit-form submit-url) "Save" {:disabled "disabled"}]
           [button #(submit-form submit-url) "Save"])]))))
