; Copyright 2010 Kasper Langer and Thomas G. Kristensen
(ns tex-service.server
  (:use ring.adapter.jetty
	clojure.pprint
	hiccup.core
	net.cgrand.moustache
	ring.middleware.file)
  (:import java.awt.image.BufferedImage
	   javax.imageio.ImageIO
	   javax.swing.JLabel
	   java.net.URLDecoder
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
    [:head [:script {:src "/static/js/jquery-1.4.2.js"}]]
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

(defn ajax-madness-app [req]
  (html-response (layout [:img {:src "tex/%5Cfrac%7B%5Cpi%7D%7B2%7D"}])))

(def main-app
     (app
      (wrap-file "public")
      (app [] ajax-madness-app
	   ["tex" &] formula-app)))

(defn run-server []
  (run-jetty (var main-app) {:port 8080 :join? false}))


