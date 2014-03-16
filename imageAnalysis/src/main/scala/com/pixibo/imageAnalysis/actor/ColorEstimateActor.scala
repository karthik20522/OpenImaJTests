package com.pixibo.imageAnalysis.actor

import org.openimaj.image.MBFImage
import akka.actor.Actor
import org.openimaj.image.feature.global.Colorfulness
import com.pixibo.imageAnalysis.model.ColorValue

class ColorEstimateActor extends Actor {
  val colorFulDetector = new Colorfulness()

  def receive = {
    case mbfImage: MBFImage => {
      mbfImage.analyseWith(colorFulDetector)
      sender ! ColorValue(colorFulDetector.getColorfulness)
    }
    case _ => sender ! ColorValue
  }
}