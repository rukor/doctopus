(ns doctopus.db.core-test
  (:require [doctopus.db.core :refer :all]
            [clojure.test :refer :all]))

(deftest db
  (testing "->kebab-keys transforms all keys to kebab-case"
    (is (= (#'doctopus.db/->kebab-keys {:key_name "lol"}) {:key-name "lol"}))
    (is (= (#'doctopus.db/->kebab-keys {:key_name "lol" :another_key "heh"})
           {:key-name "lol" :another-key "heh"}))
    (is (= (#'doctopus.db/->kebab-keys
            {:key_name "lol" :another_key "heh" :untouched-key "laffo"})
           {:key-name "lol" :another-key "heh" :untouched-key "laffo"})))
  (testing "->snake-keys transforms all keys to snake-case"
    (is (= (#'doctopus.db/->snake-keys {:key-name "lol"}) {:key_name "lol"}))
    (is (= (#'doctopus.db/->snake-keys {:key-name "lol" :another-key "heh"})
           {:key_name "lol" :another_key "heh"}))
    (is (= (#'doctopus.db/->snake-keys
            {:key-name "lol" :another-key "heh" :untouched_key "laffo"})
           {:key_name "lol" :another_key "heh" :untouched_key "laffo"})))
  (testing "add-date-fields adds updated and created fields"
    (is (= (#'doctopus.db/add-date-fields {} "right-now")
           {:updated "right-now" :created "right-now"})))
  (testing "add-date-fields does not updated created if present"
    (is (= (#'doctopus.db/add-date-fields {:created "earlier"} "right-now")
           {:updated "right-now" :created "earlier"}))))
