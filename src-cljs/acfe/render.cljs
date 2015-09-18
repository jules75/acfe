(ns acfe.render
  "Render data model to DOM."
  (:require
   [acfe.chart :as chart]
   [dommy.core :refer-macros [sel sel1] :as d]
   [goog.dom :as gdom]
   [goog.fx.Dragger]
   [clojure.string :refer [replace lower-case]]
   ))

(def gmap (atom nil))
(def gmarkers (atom #{}))
(def gpolygons (atom #{}))


(defn encode
  "Turn spaces to underscores, slashes to dashes, make all lowercase."
  [s]
  (-> s
	  (replace #"\s" "_")
	  (replace #"/" "-")
	  lower-case))


(defn create-map
  "Creates Google Map object in DOM, binds to global gmap."
  []
  (let [opts {:zoom 7}
		mapobj (google.maps.Map. (sel1 :#map) (clj->js opts))]
	(reset! gmap mapobj)
	))


(defn create-marker
  [lat lng gmap title id category click-handler]
  (let [marker (google.maps.Marker.
				(clj->js
				 {:position (google.maps.LatLng. lat lng)
				  :map gmap
				  :title title
				  :id id
				  :icon (str "/img/markers/" (encode category) ".png")
				  :category category}))]
	(.addListener google.maps.event marker "click" click-handler)
	marker))


(defn create-polygon
  [title glatlngs gmap color id click-handler hover-handler]
  (let [poly (google.maps.Polygon.
			  (clj->js
			   {:paths glatlngs
				:strokeColor color
				:strokeOpacity 1.0
				:strokeWeight 2.0
				:fillColor color
				:fillOpacity 0.2
				:id id
				:title title
				:map gmap}))]
	(.addListener google.maps.event poly "click" click-handler)
	(.addListener google.maps.event poly "mouseover" hover-handler)
	(.addListener google.maps.event poly "mouseout" #(d/hide! (d/sel1 :#area-title)))
	poly))



(defn create-markers
  [places click-handler]
  (let [f #(create-marker (:lat %) (:lng %) @gmap (:title %) (:id %) (:category %) click-handler)]
	(reset! gmarkers (doall (map f places))))
  (.setCenter @gmap (.getPosition (first @gmarkers))))



(defn update-markers!
  "Show marker if its category exists in categories."
  [gmarkers categories]
  (doseq [marker gmarkers
		  :let [flag (boolean (some #{(.-category marker)} categories))]]
	(.setVisible marker flag)))


(defn update-polygons!
  "Show polygon if its title exists in titles."
  [gpolygons titles]
  (doseq [gpoly gpolygons
		  :let [flag (boolean (some #{(.-title gpoly)} titles))]]
	(.setVisible gpoly flag)))


(defn create-polygons
  [areas click-handler hover-handler]
  (let [create-latlng #(google.maps.LatLng. (:lat %) (:lng %))
		create-poly #(create-polygon
					  (:lga %)
					  (clj->js (map create-latlng (:boundary %)))
					  @gmap
					  (:colour %)
					  (:id %)
					  click-handler
					  hover-handler)]
	(reset! gpolygons (doall (map create-poly areas)))
	))


(defn update-buttons!
  "Set button as selected if its text exists in texts."
  [selector texts]
  (doseq [button (sel selector)
		  :let [f (if (some #{(.-innerText button)} texts) d/add-class! d/remove-class!)]]
	(f button :selected)))


(defn tooltip
  "Renders a tooltip at mouse's current position."
  [event text]
  (let [x (.-clientX (.-wb event))
		y (.-clientY (.-wb event))
		offset 20]
	(->
	 (d/sel1 :#area-title)
	 (d/set-text! text)
	 (d/set-style! :left (str (+ x offset) "px"))
	 (d/set-style! :top (str (+ y offset) "px"))
	 d/show!)))


; thanks to http://antoine-nope.blogspot.com.au/2012/11/clojurescript-dom-children-like-sequence.html
(extend-type js/HTMLCollection
  ISeqable
  (-seq [array] (array-seq array 0)))


(defn render-ui
  [place-categories areas regions]

  ; make panels draggable
  (doseq [panel (d/sel [:.panel.draggable])]
	(goog.fx.Dragger. panel))

  ; show page, hide spinner
  (-> (sel1 [:#content]) (d/set-style! :visibility "visible"))
  (-> (sel1 [:#loading]) (d/set-style! :display "none"))


  (update-markers! @gmarkers place-categories)
  (update-polygons! @gpolygons areas)

  (update-buttons! "#categories button" place-categories)
  (update-buttons! "#regions button" regions)

  (when (nil? (.getItem (.-localStorage js/window) "table-mode"))
  	(chart/draw))

  )

