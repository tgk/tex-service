(ns tex-service.server
  (:use ring.adapter.jetty)
  (:import java.awt.image.BufferedImage
	   javax.imageio.ImageIO
	   javax.swing.JLabel
	   [java.awt Color Graphics2D]
	   [java.io ByteArrayOutputStream ByteArrayInputStream]
	   [org.scilab.forge.jlatexmath TeXFormula TeXIcon TeXConstants]))

(defn hello-app [req]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "Hello cruel world."})

(defn blue-app [req] 
  (let [tex "\\frac{\\pi}{2}"
	formula (TeXFormula. tex)
	icon (.createTeXIcon formula TeXConstants/STYLE_DISPLAY 20)
	img (BufferedImage. (.getIconWidth icon) (.getIconHeight icon) BufferedImage/TYPE_INT_ARGB)
	g2 (.createGraphics img)
	os (ByteArrayOutputStream.)
	label (JLabel.)]
    (.setForeground label (Color/BLACK))
    (.paintIcon icon label g2 0 0)
    (ImageIO/write img "png" os)
    {:status 200
     :headers {"Content-Type" "image/png"}
     :body (ByteArrayInputStream. (.toByteArray os))}))

(defn run-server []
  (run-jetty (var blue-app) {:port 8080 :join? false}))


