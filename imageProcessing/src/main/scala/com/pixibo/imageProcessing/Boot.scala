package com.pixibo.imageProcessing

import akka.io.IO
import spray.can.Http
import akka.actor.{ Props, Actor, ActorRefFactory, ActorRef, ActorSystem }
import concurrent.duration._
import com.pixibo.imageProcessing.routing.ImageProcessingRoutes

object Boot extends App {
  implicit val system = ActorSystem("pixiboImageProcessingAPI")
  val serviceActor = system.actorOf(Props(new ImageProcessingRoutes), name = "pixiboImageProcessingRouting")

  system.registerOnTermination {
    system.log.info("PIXIBO Image Processing actor shutdown.")
  }

  IO(Http) ! Http.Bind(serviceActor, "0.0.0.0", port = 8082)
}
