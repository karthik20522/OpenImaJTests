package api.actor

import akka.actor.Actor
import akka.actor.ActorRef

class MessagingActor(amqpConn: ActorRef) extends Actor {
  def receive = {
    case _ => ???
  }
}