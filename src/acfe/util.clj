(ns acfe.util)


(defn formatted-fact-detail
  "A fact contains one detail, which might be a string, int or decimal.
  Return detail in appropriate format."
  [fact]
  (let [whole? #(== % (int %)) ; double = to handle bigints properly
		value (:detail_value fact)
		text (:detail_text fact)]
	(cond
	 (nil? value) text
	 (whole? value) (str (int value))
	 :else (str value))))
