(ns acfe.panel
  "Handle draggable, closeable 'panels' of HTML."
  (:require
   [dommy.core :refer-macros [sel sel1] :as d]))


(defn create-panel
  "Adds new panel to DOM, returns HTML object of panel's content DIV."
  []
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
				(d/add-class! "draggable"))]
	(d/append! (d/sel1 :body) (-> div (d/append! (d/append! dragbar close)) (d/append! content)))
	content))

