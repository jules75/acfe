(ns acfe.chart
  "Unobtrusively convert marked HTML into Google Charts."
  (:require
   [dommy.core :refer-macros [sel sel1] :as d]))


(defn- htmlrow->cells
  "Given HTML row element, return vector of its cell values."
  [htmlrow]
  (vec (map #(.-innerHTML %) (.-cells htmlrow))))


(defn- string->float
  "Convert string to float if possible, otherwise return string."
  [s]
  (if (re-find #"^-{0,1}\d+\.{0,1}\d*$" (str s))
	(.parseFloat js/window s)
	s))


(defn- create-chart
  "Given marked up HTMLTableElement table-element, render Google Chart to target-element.
  chart-type is :bar-chart or :column-chart."
  [target-element table-element chart-type stacked? percent?]
  (let [raw-rows (into [] (map htmlrow->cells (-> table-element .-rows)))
		first-row (conj (first raw-rows) {:role "annotation"})
		rest-rows (map #(conj % nil) (rest raw-rows))
		final-rows (map (partial map string->float) (cons first-row rest-rows))
		data (.arrayToDataTable google.visualization (clj->js final-rows))
		opts {:title (first first-row)
			  :vAxis {:minValue 0
					  :gridlines {:color "transparent"}
					  :textStyle {:fontSize 11}
					  }
			  :legend {:position "none"}
			  :height (if (= chart-type :column-chart) 100 "auto")
			  :width (if (= chart-type :column-chart) 100 "auto")
			  :tooltip {:textStyle {:fontSize 11}}
			  :isStacked (cond
						  percent? "percent"
						  stacked? true
						  :else false)}
		chart (case chart-type
				:bar-chart (new google.visualization.BarChart target-element)
				:column-chart (new google.visualization.ColumnChart target-element)
				nil)]
	(.draw chart data (clj->js opts))
	))


(defn- draw-one
  [selector chart-type stacked? percent?]
  (doseq [table (d/sel selector)]
	(->
	 (d/create-element :div)
	 (d/add-class! "chart")
	 (d/add-class! (name chart-type))
	 (d/insert-before! table)
	 (create-chart table chart-type stacked? percent?))
	(d/remove-class! table "chart") ; so table isn't converted to chart again
	(d/hide! table)))


(defn draw
  []
  (draw-one [:.chart.bar.percent] :bar-chart true true)
  (draw-one [:.chart.bar] :bar-chart true false)
  (draw-one [:.chart.column] :column-chart false false)
  )

