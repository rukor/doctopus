(ns doctopus.doctopus-test
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.test :refer :all]
            [doctopus.db :as db]
            [doctopus.db.schema :as schema]
            [doctopus.doctopus.head :as h]
            [doctopus.doctopus :refer :all]
            [doctopus.doctopus.tentacle :as t]
            [doctopus.test-database :refer [schema-and-content-fixture]]
            [doctopus.test-utilities :as utils]
            [taoensso.timbre :as log])
  (:import [doctopus.doctopus Doctopus]
           [doctopus.doctopus.head Head]))

(use-fixtures :once schema-and-content-fixture)

(def stunt-doctopus (Doctopus. {} {}))

(deftest doctopus-test
  (testing "Can we list heads?"
    (let [heads (list-heads stunt-doctopus)]
      (is (not (empty? heads)))
      (is (= "main" (:name (first heads))))))
  (testing "Can we list tentacles?"
    (let [tent-list (list-tentacles stunt-doctopus)]
      (is (= 1 (count tent-list))
          "We should only have one of these")
      (is (= "doctopus" (:name (first tent-list)))
          "Would sure be weird if we got a different tentacle here")))
  (testing "Can we get a tentacle by its head?"
    (is (= 1 (count (list-tentacles-by-head stunt-doctopus "main")))
        "This should get one tentacle")
    (is (empty? (list-tentacles-by-head stunt-doctopus "HOOBASTANK"))
        "There should never, ever be a tentacle with this name. Ever.")))
