(ns acfe.panel
  "Handle draggable, closeable 'panels' of HTML."
  (:require
   [dommy.core :refer-macros [sel sel1] :as d]))


(defn- create-panel-helper
  "Adds new panel to DOM, returns HTML object of panel's content DIV."
  [loading?]
  (let [id (str "panel" (rand-int 1e10))
		dragbar (-> (d/create-element :p)
					(d/add-class! "dragbar"))
		close (-> (d/create-element :img)
				  (d/set-attr! :src "img/close.png")
				  (d/add-class! "close"))
		content (-> (d/create-element :div)
					(d/add-class! "content"))
		div (-> (d/create-element :div)
				(d/set-attr! :id id)
				(d/add-class! "panel")
				(d/add-class! "draggable")
				(d/add-class! (if loading? "loading" "normal")))]

	(as-> (d/append! dragbar close) $
		  (d/append! div $)
		  (d/append! $ content)
		  (d/append! (d/sel1 :body) $))

	content))


(defn create-panel
  []
  (create-panel-helper false))


(defn create-loading-panel
  "Creates 'loading' panel that will be cleaned up after normal
  panel has loaded."
  []
  (create-panel-helper true))


(defn destroy-loading-panels
  "Destroys all loading panels in DOM."
  []
  (doseq [panel (sel [:.panel.loading])]
	(d/remove! panel)))

