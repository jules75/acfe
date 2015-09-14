(ns acfe.place
  "Generate place HTML from database"
  (:require	[acfe.util :refer [formatted-fact-detail]]
			[net.cgrand.enlive-html :as e]))


(defn place-html
  "Return HTML representation of given place facts."
  [place-facts]
  (let [grouped-facts (group-by :category place-facts)
		any-fact (->> grouped-facts first val first)]
	(->
	 (e/html-resource "html/place.html")
	 (e/at
	  [:pre] (e/content (apply str grouped-facts))
	  [:h2] (e/content (:place any-fact))
	  [:table] (e/clone-for
				[category grouped-facts]
				[:caption] (e/content (key category))
				[:tr] (e/clone-for
					   [fact (val category)]
					   [:td.title] (e/content (:title fact))
					   [:td.value] (e/content (formatted-fact-detail fact))
					   ))))))
