package com.pixibo.imageAnalysis.actor

import akka.actor.Actor
import org.openimaj.image.feature.global.Naturalness
import org.openimaj.image.MBFImage
import com.pixibo.imageAnalysis.model.NaturalnessValue

class NaturalnessEstimateActor extends Actor {
  val naturalEstimator = new Naturalness()

  def receive = {
    case mbfImage: MBFImage => {
      naturalEstimator.analyseImage(mbfImage)
      sender ! NaturalnessValue(naturalEstimator.getNaturalness)
    }
    case _ => sender ! NaturalnessValue
  }
}