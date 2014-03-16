name :="imageProcessing"

version :="1.0"

scalaVersion :="2.10.3"

resolvers ++= Seq(
  "spray repo" at "http://repo.spray.io/"  
)

libraryDependencies ++= {
  val akkaV = "2.3.0"
  val sprayV = "1.3.0"
  Seq(    
   	"io.spray" % "spray-can" % sprayV,
    "io.spray" % "spray-routing" % sprayV,
    "io.spray" % "spray-testkit" % sprayV,    
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-testkit" % akkaV,
    "org.specs2" %% "specs2" % "2.2.3" % "test",
    "org.json4s" %% "json4s-native" % "3.2.6",
    "net.databinder.dispatch" %% "dispatch-core" % "0.11.0",
  	"com.sksamuel.scrimage" % "scrimage-core_2.10" % "1.3.15",
	"com.sksamuel.scrimage" % "scrimage-filters_2.10" % "1.3.15",
	"com.typesafe" %% "scalalogging-slf4j" % "1.0.1",	
  	"ch.qos.logback"   % "logback-classic"  % "1.0.3"
  )
}

seq(Revolver.settings: _*)
