(ns acfe.tabs
  "Unobtrusively convert marked HTML into tabbed content."
  (:require
   [dommy.core :refer-macros [sel sel1] :as d]))


(defn on-link-click
  "When tab link clicked, make link and target tab class 'active'."
  [e]
  (let [id (.getAttribute (.-target e) "data-target")
		links (sel [(-> e .-target .-parentElement) :li])
		tabs (sel [(-> e .-target .-parentElement .-parentElement .-parentElement) :.tab])
		target? #(= id (.getAttribute % "data-id"))
		tab (first (filter target? tabs))]
	(doseq [t tabs] (d/remove-class! t :active))
	(doseq [l links] (d/remove-class! l :active))
	(d/add-class! tab :active)
	(d/add-class! (.-target e) :active)))


(defn tabify
  "Search DOM for tabs, attach targets to nav link and ids to tabs."
  []
  (doseq [[n link tab]
		  (partition 3 (interleave (range) (sel [:div.tabs :nav :li]) (sel [:div.tabs :.tab])))
		  :let [id (str "tab" n)]]
	(d/listen! link :click on-link-click)
	(d/set-attr! link :data-target id)
	(d/set-attr! tab :data-id id)))

