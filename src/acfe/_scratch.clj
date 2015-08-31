
;
; This code is not used in production, but is useful for various things
;

(def area-ids [2 6 7 3 8 4 21 5 9 15 16 17 18 10 11 19 12 20 13 14])

(def values
  [[0.3 -14.6 0.2 4.0 14.3]
   [0.1 -15.7 1.2 5.8 16.6]
   [1.6 -14.9 -0.5 6.5 17.8]
   [0.2 -9.1 -1.2 4.4 5.0]
   [0.6 -16.2 1.0 5.4 10.6]
   [-0.2 -14.7 18.2 -0.1 -1.2]
   [0.6 -16.5 4.2 -3.2 -2.7]
   [-0.2 -14.7 6.7 -1.4 -7.6]
   [1.1 -12.1 -3.1 27.5 44.0]
   [0.4 -13.9 -6.5 4.6 12.8]
   [0.7 -13.7 -3.4 0.9 5.3]
   [0.0 -14.4 -2.0 2.4 7.4]
   [-0.1 -12.7 1.9 -1.2 7.7]
   [1.1 -14.1 3.7 -0.7 22.8]
   [1.0 -14.8 -5.5 0.6 10.8]
   [0.3 -13.1 -4.3 0.6 7.6]
   [0.7 -15.3 0.9 3.2 17.1]
   [0.4 -14.9 13.8 3.5 15.0]
   [0.0 -15.4 0.0 -0.4 18.7]
   [0.4 -14.9 -3.7 -1.0 18.8]])

(defn make-sql
  [[a b c]]
  (str "(" a ", " b ", " c "), "))


; generate SQL statements to insert facts
(->>
 (interleave
  (mapcat (partial repeat 5) area-ids)
  (cycle (range 20 25)) ; fact ids
  (flatten values))
 (partition 3)
 (map make-sql)
 (apply str)
 (str "INSERT INTO area_facts (area_id, fact_id, detail_value) VALUES"))

