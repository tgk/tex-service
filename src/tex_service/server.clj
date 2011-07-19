; Copyright 2010 Kasper Langer and Thomas G. Kristensen
(ns tex-service.server
  (:use ring.adapter.jetty
	clojure.pprint
	hiccup.core
	net.cgrand.moustache
	[ring.middleware file stacktrace reload])
  (:import java.awt.image.BufferedImage
	   javax.imageio.ImageIO
	   javax.swing.JLabel
	   [java.net URLDecoder URLEncoder]
	   [java.awt Color Graphics2D]
	   [java.io ByteArrayOutputStream ByteArrayInputStream]
	   [org.scilab.forge.jlatexmath TeXFormula TeXIcon TeXConstants]))

(defn img-response [img]
  "Renders a BufferedImage to a ring response"
  (let [os (ByteArrayOutputStream.)
	res (ImageIO/write img "png" os)
	data (.toByteArray os)
	is (ByteArrayInputStream. data)]
    {:status 200
     :headers {"Content-Type" "image/png"}
     :body is}))

(defn html-response [body]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body body})

(defn layout [& body]
  (html
   [:html
    [:title "TeX formula service"]
    [:head [:link {:rel "stylesheet" :type "text/css" :href "/static/style.css"}]]
    [:body body]]))

(defn render-tex [tex]
  "Render TeX formula (a String) to BufferedImage"
  (let [formula (TeXFormula. tex)
	icon (.createTeXIcon formula TeXConstants/STYLE_DISPLAY 20)
	img (BufferedImage. (.getIconWidth icon) (.getIconHeight icon) BufferedImage/TYPE_INT_ARGB)
	g2 (.createGraphics img)
	label (JLabel.)]
    (.setForeground label (Color/BLACK))
    (.paintIcon icon label g2 0 0)
    img))

(defn formula-app [req]
  (let [tex (URLDecoder/decode (.substring (:uri req) 1))
	img (render-tex tex)]
    (img-response img)))

(defn tex-example [tex-code]
  (let [url (str "tex/" (URLEncoder/encode tex-code))]
    (html
     [:img {:src url}]
     [:br]
     [:a {:href url} [:code tex-code]]
     [:br]
     [:br])))

(defn standard-response []
 (html
  """
<a href=\"http://github.com/tgk/tex-service\"><img style=\"position: absolute; top: 0; right: 0; border: 0;\" src=\"https://d3nwyuy0nl342s.cloudfront.net/img/abad93f42020b733148435e2cd92ce15c542d320/687474703a2f2f73332e616d617a6f6e6177732e636f6d2f6769746875622f726962626f6e732f666f726b6d655f72696768745f677265656e5f3030373230302e706e67\" alt=\"Fork me on GitHub\"></a>
  """
  [:h1 "TeX formula service"]
  [:h2 "Examples"]
  [:div
    (tex-example "\\frac{\\pi}{2}")
    (tex-example "\\sum_{i=1}^n i = \\frac{n(n-1)}{2}")]))


(defn ajax-madness-app [req]
  (html-response 
    (layout (standard-response))))

(def main-app
     (app
      (wrap-file "public")
      (wrap-stacktrace)
      (wrap-reload '(tex-service.server))
      (app [] ajax-madness-app
	   ["tex" &] formula-app)))

(defn run-server []
  (run-jetty (var main-app) {:port 8080 :join? false}))

(defn -main []
  (let [port (Integer/parseInt (System/getenv "PORT"))]
    (run-jetty main-app {:port port})))
