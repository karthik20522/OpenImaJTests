package api.actor

import akka.actor.Actor
import api.model.RequestAnalysis
import akka.actor.ActorRef
import api.Config

class ImageAnalysisActor(amqpConnection: ActorRef) extends Actor {

  def receive = {
    case RequestAnalysis(uri) => {
      context.parent ! "OK"
    }
  }
}