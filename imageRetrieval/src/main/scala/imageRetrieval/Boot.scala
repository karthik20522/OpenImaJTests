package imageRetrieval

import akka.io.IO
import spray.can.Http
import akka.actor.{ Props, Actor, ActorRefFactory, ActorRef, ActorSystem }
import concurrent.duration._
import imageRetrieval.routing.ImageRetrievalRoutes

object Boot extends App {
  implicit val system = ActorSystem("pixiboImageRetrievalAPI")
  val serviceActor = system.actorOf(Props(new ImageRetrievalRoutes), name = "pixiboImageRetrievalRouting")

  system.registerOnTermination {
    system.log.info("PIXIBO Image Retrieval actor shutdown.")
  }

  IO(Http) ! Http.Bind(serviceActor, "0.0.0.0", port = 8084)
}