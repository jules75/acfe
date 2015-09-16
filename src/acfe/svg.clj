(ns acfe.svg
  "Helper to turn areas into SVG. Not used in final product.")


(defn- render-one
  [area]
  (let [f #(* -100 (+ % 39))
		g #(* 100 (- % 143))
		fill (get {"Grampians" "#ffeeee" "Barwon South West" "#eeeeff"} (:region area))]
	(str
	 "<polygon "
	 "style=\"fill:" fill ";stroke:#000000;stroke-width:0.1px;stroke-linecap:butt;stroke-linejoin:miter;stroke-opacity:1\" "
	 "points=\""
	 (apply str
			(for [{:keys [lat lng]} (:boundary area)]
			  (str (g lng) "," (f lat) " ")
			  ))
	 "\" />")))


(defn render
  [areas]
  (map render-one areas))


