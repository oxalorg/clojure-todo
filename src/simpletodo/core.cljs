(ns simpletodo.core
    (:require
      [reagent.core :as r]
      [reagent.dom :as d]
      [reagent-forms.core :refer [bind-fields init-field value-of]]
      [lambdaisland.fetch :as fetch]))

(defn row [label input]
  [:div.row
   [:div.col-md-2 [:label label]]
   [:div.col-md-5 input]])

(def friends (r/atom ["no" "one" "here" "yet"]))

(defn reset-friends [result]
  (.log js/console (:body result))
  (let [edn (js->clj (:body result) :keywordize-keys true)
        _ (.log js/console (str "edn = " edn))]
    (reset! friends edn)))

(defn friend-source-ajax2 [text]
  (let [_ (.log js/console (str "searching: " text))
        _ (.log js/console (str "friends = " @friends))
        result (->
                (fetch/get "http://localhost:8000/api/todos/"
                           {:query-params {:text text}})
                (js/Promise.resolve)
                (.then #(reset-friends %)))]
    (.log js/console (str "result = " result))
    @friends))

(def form-template
  [:div
   (row "Best friend"
        [:div {:field           :typeahead
               :data-source     friend-source-ajax2
               :selections      friends
               :input-placeholder "Who's your best friend? You can pick only one"
               :input-class     "form-control"
               :list-class      "typeahead-list"
               :item-class      "typeahead-item"
               :highlight-class "highlighted"}])

   [:br]])

(def todos (r/atom []))

(defn get-todos-ajax []
  (->
    (fetch/get "http://localhost:8000/api/todo/")
    (js/Promise.resolve)
    (.then #(reset! todos (js->clj (:body %) :keywordize-keys true)))))

(defonce init (do
                (get-todos-ajax)))

(defn todo-form []
  (let [new-item (r/atom "") new-item-completed (r/atom false)]
    (fn []
      [:form {:on-submit (fn [e]
                           (.preventDefault e)
                           (swap! todos conj {:completed @new-item-completed :desc @new-item})
                           (reset! new-item "")
                           (reset! new-item-completed false))}
       [:input {:type "checkbox" :checked @new-item-completed
                :on-change #(reset! new-item-completed (-> % .-target .-checked))}]
       [:input {:type "text"
                :value @new-item
                :placeholder "Add a new item"
                :on-change (fn [e]
                             (reset! new-item (.-value (.-target e))))}]])))

(defn todo-item [todo]
  [:li {:style {:color (if (:completed todo) "green" "red")}} (:desc todo)])

(defn todo-list []
   [:ul
    (for [todo @todos]
      (todo-item todo))])

(defn home-page []
  [:div
   [:h2 "Lists keep it simple"]
   [:p "Add a new item below:"]
   [todo-form]
   [todo-list]
   [:div
    [:div.page-header [:h1 "Sample Form"]]
    [bind-fields
     form-template
     ]]])

;; -------------------------
;; Initialize app

(defn mount-root []
  (d/render [home-page] (.getElementById js/document "app")))

(defn ^:export init! []
  (mount-root))
