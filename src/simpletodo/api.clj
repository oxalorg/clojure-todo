(ns simpletodo.api)

(defn get-todos []
  [{:desc "Fry the garlic" :completed true}
   {:desc "Fry the onions" :completed true}
   {:desc "Boil the pasta" :completed false}])
