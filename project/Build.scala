import sbt._
import Keys._
object SbtMultiBuild extends Build {
    lazy val parent = Project(id = "parent", base = file(".")) aggregate(api, common, imageProcessing, imageAnalysis, imageRetrieval, imageSearch, faceRecognition)
	lazy val api = Project(id = "api", base = file("api"))
	lazy val common = Project(id = "common", base = file("common"))
	lazy val imageProcessing = Project(id = "imageProcessing", base = file("imageProcessing")).dependsOn(common)
	lazy val imageAnalysis = Project(id = "imageAnalysis", base = file("imageAnalysis")).dependsOn(common)
	lazy val imageRetrieval = Project(id = "imageRetrieval", base = file("imageRetrieval")).dependsOn(common)
	lazy val imageSearch= Project(id = "imageSearch", base = file("imageSearch")).dependsOn(common)
	lazy val faceRecognition = Project(id = "faceRecognition", base = file("faceRecognition")).dependsOn(common)
}