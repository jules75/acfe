(ns acfe.tabs
  "Unobtrusively convert marked HTML into tabbed content."
  (:require
   [dommy.core :refer-macros [sel sel1] :as d]))


(defn on-link-click
  [e]
  (let [id (.getAttribute (.-target e) "data-target")
		tabs (sel [(-> e .-target .-parentElement .-parentElement .-parentElement) :.tab])
		target? #(= id (.getAttribute % "data-id"))
		tab (first (filter target? tabs))]
	(doseq [t tabs] (d/remove-class! t :active))
	(d/add-class! tab :active)
	))


(defn tabify
  []

  ; attach targets to nav links and ids to tabs
  (doseq [[n link tab]
		  (partition 3 (interleave (range) (sel [:div.tabs :nav :li]) (sel [:div.tabs :.tab])))
		  :let [id (str "tab" n)]]
	(d/listen! link :click on-link-click)
	(d/set-attr! link :data-target id)
	(d/set-attr! tab :data-id id))

  )

