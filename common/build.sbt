name := "common"

version := "1.0"

scalaVersion := "2.10.3"

resolvers ++= Seq(
  "spray repo" at "http://repo.spray.io/"
)

libraryDependencies ++= {
  val akkaV = "2.3.0"
  val sprayV = "1.3.0"
  Seq(   
    "net.databinder.dispatch" %% "dispatch-core" % "0.11.0",
	"com.typesafe" %% "scalalogging-slf4j" % "1.0.1",
  	"ch.qos.logback"   % "logback-classic"  % "1.0.3"  	    
  )
}