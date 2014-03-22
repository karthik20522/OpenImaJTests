package imageAnalysis.actor

import akka.actor.Actor
import imageAnalysis.model.ContrastValue
import org.openimaj.image.feature.global.RGBRMSContrast
import org.openimaj.image.MBFImage

class ContrastEstimateActor extends Actor {
  def receive = {
    case mbfImage: MBFImage => {
      val contrastEstimator = new RGBRMSContrast()
      contrastEstimator.analyseImage(mbfImage)
      sender ! ContrastValue(contrastEstimator.getContrast())
    }
    case _ => sender ! ContrastValue
  }
}