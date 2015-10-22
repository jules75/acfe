(defproject acfe "0.16"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
			:url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[compojure "1.4.0"]
				 [org.clojure/clojure "1.7.0"]
				 [org.clojure/data.json "0.2.6"]
				 [org.clojure/clojurescript "0.0-3308"]
				 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
				 [mysql/mysql-connector-java "5.1.32"]
				 [org.clojure/java.jdbc "0.4.1"]
				 [yesql "0.4.2"]
				 [ring "1.4.0"]
				 [ring-basic-authentication "1.0.5"]
				 [bk/ring-gzip "0.1.1"]
				 [prismatic/dommy "1.1.0"]
				 [enlive "1.1.6"]
				 [cljs-ajax "0.3.13"]]
  :plugins [[lein-ring "0.9.6"]
			[lein-cljsbuild "1.0.6"]]
  :ring {:handler acfe.core/app
		 :port 3001}
  :cljsbuild
  {:builds
   {:app
	{:source-paths ["src-cljs"]
	 :compiler
	 {:optimizations :simple
	  :output-to "resources/public/js/out/app.js"
	  :pretty-print true}}}})
