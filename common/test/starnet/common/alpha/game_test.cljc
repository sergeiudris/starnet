(ns starnet.common.alpha.game-test
  (:require
   [clojure.spec.alpha :as s]
   [clojure.spec.gen.alpha :as sgen]
   [clojure.spec.test.alpha :as stest]
   [clojure.test.check :as tc]
   [clojure.test.check.generators :as gen]
   [clojure.test.check.properties :as prop]
   [clojure.test.check.clojure-test :refer [defspec]]
   [clojure.test :as test :refer [is testing run-tests deftest]]
   [starnet.common.alpha.game :refer [make-game-state next-state-game]]))

(deftest make-game-state-tests
  (testing "generates valid :g/game"
    (is (s/valid? :g/game
                  (make-game-state)))))

(deftest all-specchecks
  (testing "running spec.test/check via stest/enumerate-namespace"
    (let [summary (-> #?(:clj (stest/enumerate-namespace 'starnet.common.alpha.game)
                         :cljs 'starnet.common.alpha.game)
                      (stest/check {:clojure.spec.test.check/opts {:num-tests 10}})
                      (stest/summarize-results))]
      (is (not (contains? summary :check-failed))))))

(deftest make-game-state-speccheck
  (testing "running spec.test/check"
    (let [summary (-> (stest/check `make-game-state
                                   {:clojure.spec.test.check/opts {:num-tests 10}})
                      (stest/summarize-results))]
      (is (not (contains? summary :check-failed))))))

(deftest next-state-game-tests
  (testing "event :ev.g.u/create"
    (is (s/valid? :g/game (next-state-game (s/conform :g/game (make-game-state))
                                           (gen/generate gen/uuid)
                                           {:ev/type :ev.g.u/create
                                            :u/uuid  (gen/generate gen/uuid)}))))
  (testing "random :g/game and :ev.g.u/create event "
    (is (s/valid? :g/game (next-state-game (gen/generate (s/gen :g/game))
                                           (gen/generate gen/uuid)
                                           (gen/generate (s/gen :ev.g.u/create)))))))

(comment

  (run-tests)
  (all-specchecks)
  (make-game-state-speccheck)

  (list (reduce #(assoc %1 (keyword (str %2)) %2) {} (range 0 100)))
  
  (next-state-game-tests)
  (make-game-state-tests)
  ;;
  )