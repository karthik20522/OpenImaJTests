import sbt._
import Keys._
object SbtMultiBuild extends Build {
    lazy val parent = Project(id = "parent", base = file(".")) aggregate(api, imageProcessing, imageAnalysis, imageRetrieval, imageSearch)
	lazy val api = Project(id = "api", base = file("api"))
    lazy val imageProcessing = Project(id = "imageProcessing", base = file("imageProcessing"))
	lazy val imageAnalysis = Project(id = "imageAnalysis", base = file("imageAnalysis"))
	lazy val imageRetrieval = Project(id = "imageRetrieval", base = file("imageRetrieval"))
	lazy val imageSearch= Project(id = "imageSearch", base = file("imageSearch"))
}