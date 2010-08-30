(ns tex-service.server
  (:use ring.adapter.jetty
	clojure.pprint
	hiccup.core)
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
  (let [body (html [:img {:src "%5Cfrac%7B%5Cpi%7D%7B2%7D"}])]
    {:status 200
     :headers {"Content-Type" "text/html"}
     :body body}))

(defn app [req]
  (if (= "/" (:uri req))
    (ajax-madness-app req)
    (formula-app req)))

(defn run-server []
  (run-jetty (var app) {:port 8080 :join? false}))


