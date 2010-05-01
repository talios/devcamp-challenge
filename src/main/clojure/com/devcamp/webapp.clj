(ns com.devcamp.webapp
  (:gen-class :extends javax.servlet.http.HttpServlet)

  (:use clojure.contrib.json.write)
  (:use clojure.contrib.duck-streams)
  (:use clojure.contrib.logging)
  (:use compojure.control)
  (:use compojure.html.gen)
  (:use compojure.html.page-helpers)
  (:use compojure.html.form-helpers)
  (:use compojure.http.helpers)
  (:use compojure.http.multipart)
  (:use compojure.http.routes)
  (:use compojure.http.request)
  (:use compojure.http.servlet)
  (:use compojure.http.session)
  (:use compojure.map-utils)
  (:use compojure.validation)

  )

(def *QUESTIONS* (ref [{:id "0c6d5bfc-5e1f-455e-b46d-3b873868519a" :votes 0 :question "i have clearly understood how i can progress in my workplace"}
                       {:id "71b78f5a-0607-45b7-891f-f092de885737" :votes 0 :question "i have a nemesis at work"}
                       {:id "d8585927-235e-494f-80b8-fe4119e5e031" :votes 0 :question "i get enough training and mentoring in my work"}
                       {:id "1a29b97a-0f0f-4fd6-913e-61ab6162f737" :votes 0 :question "i like the taste of freshly roasted graduate developers"}
                       {:id "4d54afd9-0db2-4449-8c50-aeda32c3e989" :votes 0 :question "i am physically attracted to my direct manager"}
                       {:id "d01a95cd-2ed3-44c3-9aac-7bd3e1d1dc51" :votes 0 :question "friday drinks aren't over until you fallover"}
                       ]))

(defn vote-question
  "Vote for a question"
  [id vote]
  (fn [question-map]
    (if (= id (:id question-map))
      (assoc question-map :votes (+ (:votes question-map) (Integer/valueOf vote)))
      question-map)))

(defroutes devcamp-app
  (GET "/devcamp/"
    {:status 200
     :body (json-str @*QUESTIONS*)})

  (POST "/devcamp/vote"
    (let [voteValue (Integer/valueOf (:vote params))]
      (if (not (= "" (:id params)))
        (if (and (> voteValue 0) (< voteValue 6))
          (if (= "confirm" (:confirm params))
            (dosync
              (ref-set *QUESTIONS* (map (vote-question (:id params) (:vote params)) @*QUESTIONS*))
              {:status 200 :body (json-str {:status "Voted"})})
            {:status 412 :body (json-str {:status "Please confirm"})})
          {:status 412 :body (json-str {:status "Vote must be between 1 and 5"})})
      {:status 412 :body (json-str {:status "Id must be specified"})}))))

(defservice devcamp-app)
