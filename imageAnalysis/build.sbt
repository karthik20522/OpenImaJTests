name := "imageAnalysis"

version := "1.0"

scalaVersion :="2.10.3"

resolvers ++= Seq(
  "spray repo" at "http://repo.spray.io/",
  "openImaJ repo" at "http://maven.openimaj.org"
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
	"com.typesafe" %% "scalalogging-slf4j" % "1.0.1",
  	"ch.qos.logback"   % "logback-classic"  % "1.0.3",
  	"com.drewnoakes" % "metadata-extractor" % "2.6.2",
    "org.openimaj" % "image-processing" % "1.2",
    "org.openimaj" % "faces" % "1.2",
    "com.github.nscala-time" %% "nscala-time" % "0.8.0"
  )
}

seq(Revolver.settings: _*)