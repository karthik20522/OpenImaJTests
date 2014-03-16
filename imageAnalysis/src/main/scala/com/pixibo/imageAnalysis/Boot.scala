package com.pixibo.imageAnalysis

import akka.io.IO
import spray.can.Http
import akka.actor.{ Props, Actor, ActorRefFactory, ActorRef, ActorSystem }
import concurrent.duration._
import com.pixibo.imageAnalysis.routing.ImageAnalysisRoutes

object Boot extends App {
  implicit val system = ActorSystem("pixiboImageAnalysisAPI")
  val serviceActor = system.actorOf(Props(new ImageAnalysisRoutes), name = "pixiboImageAnalysisRouting")

  system.registerOnTermination {
    system.log.info("PIXIBO Image Analysis actor shutdown.")
  }

  IO(Http) ! Http.Bind(serviceActor, "0.0.0.0", port = 8083)
}
