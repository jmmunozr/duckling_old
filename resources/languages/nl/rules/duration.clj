; Durations / Periods

(
  "second (unit-of-duration)"
  #"(?i)sec(onde)?n?"
  {:dim :unit-of-duration
   :grain :second}

  "minute (unit-of-duration)"
  #"(?i)min|minuut|minuten"
  {:dim :unit-of-duration
   :grain :minute}

  "hour (unit-of-duration)"
  #"(?i)uur"
  {:dim :unit-of-duration
   :grain :hour}

  "day (unit-of-duration)"
  #"(?i)dagen?"
  {:dim :unit-of-duration
   :grain :day}

  "week (unit-of-duration)"
  #"(?i)weken?"
  {:dim :unit-of-duration
   :grain :week}

  "month (unit-of-duration)"
  #"(?i)maanden?"
  {:dim :unit-of-duration
   :grain :month}

  "year (unit-of-duration)"
  #"(?i)jaar?"
  {:dim :unit-of-duration
   :grain :year}

   "quarter of an hour"
  [#"(?i)(1/4\s?uur?|(a\s)?een kwartier)"]
  {:dim :duration
   :value (duration :minute 15)}

   "half an hour"
  [#"(?i)(1/2\s?uur?|half uur)"]
  {:dim :duration
   :value (duration :minute 30)}

   "three-quarters of an hour"
  [#"(?i)(3/4\s?uur?|drie kwartier)"]
  {:dim :duration
   :value (duration :minute 45)}

  "fortnight" ;14 days
  #"(?i)veertien dagen"
  {:dim :duration
   :value (duration :day 14)}

  "<integer> <unit-of-duration>"
  [(integer 0) (dim :unit-of-duration)]; duration can't be negative...
  {:dim :duration
   :value (duration (:grain %2) (:value %1))}

  "<integer> more <unit-of-duration>"
  [(integer 0) #"(?i)meer|minder" (dim :unit-of-duration)]; would need to add fields at some point
  {:dim :duration
   :value (duration (:grain %3) (:value %1))}

  ; TODO handle cases where ASR outputs "1. 5 hours"
  ; but allowing a space creates many false positive
  "number.number hours" ; in 1.5 hour but also 1.75
  [#"(\d+)\.(\d+)" #"(?i)uren?"] ;duration can't be negative...
  {:dim :duration
   :value (duration :minute (int (+ (quot (* 6 (Long/parseLong (second (:groups %1)))) (java.lang.Math/pow 10 (- (count (second (:groups %1))) 1))) (* 60 (Long/parseLong (first (:groups %1)))))))}

  "<integer> and an half hours"
  [(integer 0) #"(?i)anderhalf uur?"] ;duration can't be negative...
  {:dim :duration
   :value (duration :minute (+ 30 (* 60 (:value %1))))}

  "a <unit-of-duration>"
  [#"(?i)een?" (dim :unit-of-duration)]
  {:dim :duration
   :value (duration (:grain %2) 1)}

  "in <duration>"
  [#"(?i)in" (dim :duration)]
  (in-duration (:value %2))

  "for <duration>"
  [#"(?i)voor" (dim :duration)]
  (in-duration (:value %2))

  "after <duration>"
  [#"(?i)na" (dim :duration)]
  (merge (in-duration (:value %2)) {:direction :after})

  "<duration> from now"
  [(dim :duration) #"(?i)van (vandaag|nu)"]
  (in-duration (:value %1))

  "<duration> ago"
  [(dim :duration) #"(?i)geleden"]
  (duration-ago (:value %1))

  "<duration> after <time>"
  [(dim :duration) #"(?i)na" (dim :time)]
  (duration-after (:value %1) %3)

  "<duration> before <time>"
  [(dim :duration) #"(?i)voor" (dim :time)]
  (duration-before (:value %1) %3)

  "about <duration>" ; about
  [#"(?i)(rond)" (dim :duration)]
  (-> %2
    (merge {:precision "approximate"}))

  "exactly <duration>" ; sharp
  [#"(?i)precies" (dim :duration)]
  (-> %2
    (merge {:precision "exact"}))

)
