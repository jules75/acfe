(ns acfe.area
  "Generate area HTML from database"
  (:require [acfe.util :refer [formatted-fact-detail]]
			[net.cgrand.enlive-html :as e]
			[clojure.string :refer [replace]]
			[yesql.core :refer [defqueries]]
			))

(defqueries "sql/queries.sql")


(defn- industry-html
  "Return HTML representation of given area's industry data."
  [id config]
  (let [facts (find-industry-facts-by-area-id (:db config) id)
		grouped-facts (group-by :fact_category_id facts)
		cost #(apply + (map :detail_value %))
		qualifications (map :title (first (vals grouped-facts)))
		top-five-industries (->> grouped-facts vals (sort-by cost) (take-last 5) reverse)]
	(->
	 (e/html-resource "html/industry.html")

	 (e/at
	  [:table :thead :td]
	  (e/clone-for
	   [qual (cons "Main employment industries by qualifications (ABS Census 2011)" qualifications)]
	   [:td] (e/content qual)
	   ))

	 (e/at
	  [:table :tbody :tr]
	  (e/clone-for
	   [industry top-five-industries]
	   [:td]
	   (e/clone-for
		[fact (cons {:detail_text (:category (first industry))} industry)]
		[:td] (e/content (formatted-fact-detail fact))
		)))

	 )))


(defn nl2br
  "Turn line breaks into <br> tags."
  [s]
  (replace s #"[\r\n]" "<br>"))


(defn area-html
  "Return HTML representation of given area facts."
  [id config]
  (let [area-facts (find-facts-by-area-id (:db config) id)
		priority-category-id 5
		learning-category-id 15
		region-priority-facts (find-fact-averages-by-region-and-category (:db config) (:region_id (first area-facts)) priority-category-id)
		grouped-facts (group-by :category area-facts)

		; TODO: eliminate hard-coded category ids
		fact-categories (find-fact-categories (:db config))
		find-by-id (fn [id] (:title (first (filter #(= id (:id %)) fact-categories))))
		cat1 (find-by-id 4)
		cat2 (find-by-id 5)
		cat3 (find-by-id 15)
		cat4 (find-by-id 0)

		about-text (get-in grouped-facts [cat4 0 :detail_text])
		population-facts (get grouped-facts cat1)
		population (int (reduce + (map :detail_value population-facts)))
		area-priority-facts (get grouped-facts cat2)
		learning-facts (get grouped-facts cat3)
		region-learning-facts (find-fact-averages-by-region-and-category (:db config) (:region_id (first learning-facts)) learning-category-id)
		merged-priority-facts (map vector (sort-by :fact_id region-priority-facts) (sort-by :fact_id area-priority-facts))
		]
	(->
	 (e/html-resource "html/area.html")

	 (e/at
	  [:h2 :span] (e/content (:area (first area-facts)))
	  [:table#population :thead :td]
	  (e/clone-for
	   [fact (cons {:title (str cat1 ": " population)} population-facts)]
	   [:td] (e/content (:title fact)))
	  [:table#population :tbody :tr.area :td]
	  (e/clone-for
	   [fact (cons {:detail_text "This LGA"} population-facts)]
	   [:td] (e/content (formatted-fact-detail fact))
	   ))

	 (e/at
	  [:table#priority]
	  (e/clone-for
	   [[region-fact area-fact] merged-priority-facts]
	   [:thead :td.title] (e/content (:title area-fact))
	   [:tbody :tr.area :td.data] (e/content (formatted-fact-detail area-fact))
	   [:tbody :tr.region :td.data] (e/content (formatted-fact-detail region-fact))
	   ))

	 (e/at
	  [:table#learning :thead :td]
	  (e/clone-for
	   [fact (cons {:title cat3} learning-facts)]
	   [:td] (e/content (:title fact)))
	  [:table#learning :tbody :tr.area :td]
	  (e/clone-for
	   [fact (cons {:detail_text "This LGA"} learning-facts)]
	   [:td] (e/content (formatted-fact-detail fact))
	   ))

	 (e/at
	  [:div#industry] (e/content (industry-html id config)))

	 (e/at
	  [:.about] (e/html-content (nl2br about-text))) ; TODO: susceptible to html injection

	 )))


