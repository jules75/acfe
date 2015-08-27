(ns acfe.core
  (:require [clojure.data.json :as json]
			[clojure.java.io :as io]
			[clojure.string :refer [split]]
			[compojure.core :refer [defroutes GET POST]]
			[compojure.route :refer [resources not-found]]
			[compojure.handler :refer [site]]
			[ring.middleware.basic-authentication :refer [wrap-basic-authentication]]
			[ring.middleware.gzip :refer [wrap-gzip]]
			[net.cgrand.enlive-html :as e]
			[yesql.core :refer [defqueries]]
			))


(def config
  (->>
   (slurp "config.clj")
   read-string
   (into {}) ; ensure no code execution
   ))


(defn auth?
  "For basic HTML authentication"
  [username password]
  (= password (-> config :html-auth :password)))


(defqueries "sql/queries.sql")


(def to-double #(Double/parseDouble %))
(def to-int #(Integer/parseInt %))


(defn boundary->coords
  "Take boundary string (see regions.csv), return coords."
  [s]
  (->>
   (split s #"\s")
   (map #(split % #","))
   (map drop-last)
   (map #(map to-double %))
   (map #(map (partial format "%.5f") %))
   (map #(map to-double %))
   (map #(zipmap [:lng :lat] %))))


(defn get-areas
  "Get areas from database, replacing boundary string with individual coords."
  []
  (->>
   (find-areas (:db config))
   (map #(assoc % :boundary (boundary->coords (:boundary-string %))))
   (map #(dissoc % :boundary-string))))


(e/defsnippet category-button-snippet "html/_layout.html"
  [:#categories :li]
  [title]
  [:button] (e/content title))

(e/defsnippet region-button-snippet "html/_layout.html"
  [:#regions :li]
  [title]
  [:button] (e/content title))

(e/defsnippet lga-button-snippet "html/_layout.html"
  [:#lgas :li]
  [title]
  [:button] (e/content title))

(e/deftemplate main-template "html/_layout.html"
  []
  [:#categories :ul] (e/content (->> (find-places (:db config)) (map :category) distinct sort (map category-button-snippet)))
  [:#regions :ul] (e/content (->> (get-areas) (map :region) distinct sort (map region-button-snippet)))
  )


(defn place-html
  "Generate html of facts for given place ID."
  [id]
  (let [grouped-facts (group-by :category (find-facts-by-place-id (:db config) id))
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
					   [:td.value] (e/content (if (nil? (:detail_text fact)) (str (:detail_value fact)) (:detail_text fact)))
					   ))))))


(defn area-html
  "Generate html of facts for given area ID."
  [id]
  (let [area-facts (find-facts-by-area-id (:db config) id)]
	(->
	 (e/html-resource "html/area.html")
	 (e/at
	  [:h2 :span] (e/content (:area (first area-facts)))
	  [:table :thead :td]
	  (e/clone-for
	   [fact (cons nil area-facts)]
	   [:td] (e/content (:title fact)))
	  [:table :tbody :tr.area :td]
	  (e/clone-for
	   [fact (cons {:detail_text "This LGA"} area-facts)]
	   [:td] (e/content (if (nil? (:detail_text fact)) (str (:detail_value fact)) (:detail_text fact)))
	   )))))


(defroutes routes
  (GET "/data/places.clj" [] (str "#{" (reduce str (find-places (:db config))) "}"))
  (GET "/data/areas.clj" [] (str "#{" (apply str (get-areas)) "}"))
  (GET "/api/place.html" [id] (e/emit* (place-html id)))
  (GET "/api/area.html" [id] (e/emit* (area-html id)))
  (GET "/" [] (main-template))
  (resources "/")
  (not-found "Page not found"))


(def app (wrap-gzip (wrap-basic-authentication (site routes) auth?)))

