(ns scimmer.utils)

(defn debounce
  "Whenever `f` is run the timer will be reset to 0, if the timer reach `t`
   then the `tf` function is run.
   It return the function `f`"
  [f tf t]
  (let [timeout (atom 0)
        reset-or-run-fn (fn [& args]
                          (js/clearTimeout @timeout)
                          (reset! timeout (js/setTimeout tf t)))]
    (comp reset-or-run-fn f)))

(comment
  (def my-fn (debounce #(println "hi" %) #(println "Finished") 2000))
  (my-fn "Oussama"))





