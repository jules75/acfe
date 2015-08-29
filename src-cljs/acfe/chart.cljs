(ns acfe.chart
  "Unobtrusively convert marked HTML into Google Charts."
  (:require
   [dommy.core :refer-macros [sel sel1] :as d]))


(defn htmlrow->cells
  "Given HTML row element, return vector of its cell values."
  [htmlrow]
  (vec (map #(.-innerHTML %) (.-cells htmlrow))))


(defn string->float
  "Convert string to float if possible, otherwise return string."
  [s]
  (if (re-find #"^-{0,1}\d+\.{0,1}\d*$" (str s))
	(.parseFloat js/window s)
	s))


(defn create-chart
  "Given marked up HTMLTableElement table-element, render Google Chart to target-element.
  chart-type is :bar-chart or :column-chart."
  [target-element table-element chart-type stacked?]
  (let [raw-rows (into [] (map htmlrow->cells (-> table-element .-rows)))
		first-row (conj (first raw-rows) {:role "annotation"})
		rest-rows (map #(conj % nil) (rest raw-rows))
		final-rows (map (partial map string->float) (cons first-row rest-rows))
		data (.arrayToDataTable google.visualization (clj->js final-rows))
		opts {:title (first first-row)
			  :legend {:position "top" :maxLines 3}
			  :isStacked (if stacked? "percent" false)}
		chart (case chart-type
				:bar-chart (new google.visualization.BarChart target-element)
				:column-chart (new google.visualization.ColumnChart target-element)
				nil)]
	(.draw chart data (clj->js opts))
	))


(defn draw
  []

  (doseq [table (d/sel [:.chart.bar.stacked])]
	(->
	 (d/create-element :div)
	 (d/add-class! "chart")
	 (d/insert-before! table)
	 (create-chart table :bar-chart true))
	(d/remove-class! table "chart") ; so table isn't converted to chart again
	(d/hide! table))

  (doseq [table (d/sel [:.chart.column])]
	(->
	 (d/create-element :div)
	 (d/add-class! "chart")
	 (d/insert-before! table)
	 (create-chart table :column-chart false))
	(d/remove-class! table "chart")
	(d/hide! table))

  )

