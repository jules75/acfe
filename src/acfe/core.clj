(ns acfe.core
  (:require [acfe.area :refer [area-html]]
			[acfe.place :refer [place-html]]
			[acfe.util :refer [formatted-fact-detail]]
			[clojure.data.json :as json]
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
  [:#categories :ul] (e/content (->> (find-places (:db config)) (map :category) distinct (map category-button-snippet)))
  [:#regions :ul] (e/content (->> (get-areas) (map :region) distinct sort (map region-button-snippet)))

  ; top-level facts dropdown
  [:#facts :select.parent :option]
  (e/clone-for
   [category (group-by :category (find-area-fact-names (:db config)))]
   [:option] (e/content (key category)))

  ; child facts dropdowns
  [:#facts :select.child]
  (e/clone-for
   [category (group-by :category (find-area-fact-names (:db config)))]
   [:select.child] (e/set-attr :data-parent (key category))
   [:select.child :option]
   (e/clone-for
	[fact (val category)]
	[:option] (e/content (:title fact))
	))

  )


(defroutes routes
  (GET "/data/places.clj" [] (str "#{" (reduce str (find-places (:db config))) "}"))
  (GET "/data/areas.clj" [] (str "#{" (apply str (get-areas)) "}"))
  (GET "/api/place.html" [id] (e/emit* (place-html (find-facts-by-place-id (:db config) id))))
  (GET "/api/area.html" [id] (e/emit* (area-html id config)))
  (GET "/" [] (main-template))
  ;(GET "/tabs-test" [] (e/emit* (e/html-resource "html/tabs-test.html")))
  (resources "/")
  (not-found "Page not found"))


(def app (wrap-gzip (wrap-basic-authentication (site routes) auth?)))

