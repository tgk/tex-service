(defproject tex-service "0.0.1-SNAPSHOT"
  :description "A clojure web service for generating LaTeX formulas."
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
		 [net.sf.alxa/jlatexmath "0.9.1-SNAPSHOT"]]
  :dev-dependencies [[swank-clojure "1.3.0-SNAPSHOT"]]
  :repositories {"alxa-repo" "http://alxa.sourceforge.net/m2"})