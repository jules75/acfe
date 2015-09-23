(ns acfe.dropdown
  "Unobtrusively handle parent/child dropdowns."
  (:require
   [dommy.core :refer-macros [sel sel1] :as d]))


(defn- on-parent-select-change
  [e]
  (let [text (-> e .-target .-value)]
	(doseq [o (sel [:select.child])]
	  (if
		(= text (d/attr o "data-parent"))
		(d/remove-class! o "hidden")
		(d/add-class! o "hidden"))
	  )))


(defn- on-child-select-change
  [e]
  (this-as this (.log js/console this)))


(defn create-listeners
  []
  (doseq [o (sel [:select.parent])] (d/listen! o :change on-parent-select-change))
  (doseq [o (sel [:select.child])] (d/listen! o :change on-child-select-change))
  )

