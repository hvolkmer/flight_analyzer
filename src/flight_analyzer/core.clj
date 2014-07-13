(ns flight-analyzer.core
  (:use (incanter core stats charts io))
  (:require [clj-time.core :as t]
            [clj-time.coerce :as c]))

(defn read-from-file-with-trusted-contents [filename]
  (with-open [r (java.io.PushbackReader.
                 (clojure.java.io/reader filename))]
    (binding [*read-eval* false]
      (read r))))

(defn enroute-altitude-limit
  "create the top and bottom limits for the enroute altitde. IFR standards are +/- 100 ft of altitude."
  [flight-profile time limit]
  (let [altitude
        (->> (:enroute flight-profile)
             (map (fn [leg] [(t/interval (c/from-string (:start leg)) (c/from-string (:end leg))) (:altitude leg)]))
             (filter (fn [leg] (t/within? (first leg)  (c/from-string time)) ))
             (last)
             (last))]
    (if altitude
      (+ altitude limit)
      nil)))

(defn add-limit-values-to-data
  "Add top and bottom limits for timestamps that are considered enroute as per flight profile"
  [plotData flight-profile]
  (col-names
   (conj-cols
    ($ :elevation plotData)
    ($map (fn [x] (enroute-altitude-limit flight-profile x 100)) :time plotData)
    ($map (fn [x] (enroute-altitude-limit flight-profile x -100)) :time plotData)
    ($map c/to-long :time plotData))
   [:elevation :top :bottom :time]))

(defn create-chart
  "Creates the chart. Displayed data:
     x-axis: Time
     y-axis: Altitude in ft (Actual gps data, +100 ft line, -100 ft line based on planned enroute altitude"
  [data]
  (doto
      (time-series-plot :time :elevation :x-label "Time" :y-label "Altitude (ft)" :title "Altitude over time" :data data)
    (add-lines ($ :time data) ($ :top data))
    (add-lines ($ :time data) ($ :bottom data))))

(defn create-dataset [filename]
  (read-dataset filename
                :delim \space
                :header true))

(defn create-chart-from-data [file]
  (let [flight-profile  (read-from-file-with-trusted-contents file)
        data (create-dataset (:data-file flight-profile))
        data-with-limits (add-limit-values-to-data data flight-profile)]
    (create-chart data-with-limits)))

(defn -main [& args]
  (view (create-chart-from-data (first args))))
