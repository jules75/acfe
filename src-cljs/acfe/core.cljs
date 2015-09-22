(ns acfe.core
  "Data model, should not be aware of DOM."
  (:require
   [acfe.panel :as panel]
   [acfe.render :as render]
   [acfe.tabs :refer [tabify]]
   [dommy.core :refer-macros [sel sel1] :as d]
   [clojure.set :refer [difference union]]
   [cljs.reader :refer [read-string]]
   [ajax.core :refer [GET POST]]))



; =============================
; DATA MODEL

; loaded once from server
(def places (atom #{}))
(def areas (atom #{}))

; user-selected types of places/areas to display (as strings)
(def selected-place-categories (atom #{}))
(def selected-regions (atom #{}))


(defn selected-area-titles
  "Return area titles that belong to any of regions."
  [areas regions]
  (map :lga (filter #(some (set regions) #{(:region %)}) areas)))


(declare create-listeners)
(defn update-and-render
  "Create listeners and render model to DOM."
  []
  (create-listeners)
  (render/render-ui
   @selected-place-categories
   (selected-area-titles @areas @selected-regions)
   @selected-regions)
  (tabify))


(defn on-button-click
  "When category/region button is clicked, update model and render."
  [e]
  (let [section-id (-> e .-target .-parentElement .-parentElement .-parentElement .-id)
		m {"categories" selected-place-categories "regions" selected-regions}
		text (-> e .-target .-innerText)
		atm (get m section-id)
		f (if (some #{text} @atm) difference union)]
	(swap! atm f #{text})
	(update-and-render)))


(defn on-panel-close
  "Close panel when x is clicked."
  [e]
  (-> e .-target .-parentElement .-parentElement d/remove!))


(defn create-listeners
  "Create button and close listeners. Map marker and polygon listeners
  are created on creation."
  []
  (doseq [b (sel :button)] (d/listen! b :click on-button-click))
  (doseq [i (d/sel [:img.close])] (d/listen! i :click on-panel-close)))


(defn on-facts-load
  "When place/area facts are retrieved from server, render directly to DOM."
  [response]
  (panel/destroy-loading-panels)
  (d/set-html! (panel/create-panel) response)
  (update-and-render))


(defn on-place-click
  [e]
  (panel/create-loading-panel)
  (this-as
   this
   (GET (str "/api/place.html?id=" (.-id this))
		{:handler on-facts-load})))


(defn on-area-click
  [e]
  (panel/create-loading-panel)
  (this-as
   this
   (GET (str "/api/area.html?id=" (.-id this))
		{:handler on-facts-load})))


(defn on-map-element-hover
  "Display toolip when hovering over marker/polygon."
  [e]
  (this-as this (render/tooltip e (.-title this))))


(defn on-places-load
  "When all places are retrieved from server, add to model, create map and markers."
  [response]
  (reset! places (read-string response))
  (render/create-map)
  (render/create-markers @places on-place-click)
  (update-and-render))


(defn on-areas-load
  "When all areas are retrieved from server, add to model and create polygons."
  [response]
  (reset! areas (read-string response))
  (render/create-polygons @areas on-area-click on-map-element-hover)
  (update-and-render))


(defn disadvantaged-suburbs-hack
  "Move last categories button down a bit so it looks separate from the others."
  []
  (-> (d/create-element :li)
	  (d/set-style! :height "20px")
	  (d/insert-before! (last (sel [:#categories :li])))))


(defn init
  []
  (disadvantaged-suburbs-hack)
  (GET "/data/places.clj" {:handler on-places-load})
  (GET "/data/areas.clj" {:handler on-areas-load}))

