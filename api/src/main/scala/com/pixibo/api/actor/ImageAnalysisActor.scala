package com.pixibo.api.actor

import akka.actor.Actor
import com.pixbo.api.model.RequestAnalysis
import akka.actor.ActorRef
import com.pixibo.api.Config

class ImageAnalysisActor(amqpConnection: ActorRef) extends Actor {

  def receive = {
    case RequestAnalysis(uri) => {
      context.parent ! "OK"
    }
  }
}