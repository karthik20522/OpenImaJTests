package api

import akka.io.IO
import spray.can.Http
import akka.actor.{ Props, ActorSystem }
import api.routing.RestRouting
import concurrent.duration._
import akka.actor.actorRef2Scala

object Boot extends App {
  implicit val system = ActorSystem("pixiboAPI")
  val serviceActor = system.actorOf(Props(new RestRouting), name = "pixiboRestRouting")

  system.registerOnTermination {
    system.log.info("PIXIBO actor shutdown.")
  }

  IO(Http) ! Http.Bind(serviceActor, "0.0.0.0", port = 8081)
}

