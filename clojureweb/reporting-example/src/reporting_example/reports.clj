(ns reporting-example.reports
  (:require [clj-pdf.core :refer [pdf template]]
            [reporting-example.models.db :as db]))

(def employee-template
  (template [$name $occupation $place $country]))

(def employee-template-paragraph
  (template
            [:paragraph
             [:heading {:style {:size 15}} $name]
             [:chunk {:style :bold} "occupation: "] $occupation "\n"
             [:chunk {:style :bold} "place:  "] $place "\n"
             [:chunk {:style :bold} "country:  "] $country "\n"
             [:spacer]]))

(defn table-report [out]
  (pdf
    [{:header "Employee List"}
     (into [:table
            {:border false
             :cell-border false
             :header [{:color [0 150 150]} "Name" "Occupation" "Place" "Country"]}]
           (employee-template (db/read-employees)))]
    out))

(defn list-report [out]
  (pdf
    [{}
     [:heading {:size 10} "Employees"]
     [:line]
     [:spacer]
     (employee-template-paragraph (db/read-employees))]
    out))
