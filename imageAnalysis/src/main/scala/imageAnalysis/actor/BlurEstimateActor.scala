package imageAnalysis.actor

import akka.actor.Actor
import org.openimaj.image.FImage
import akka.actor.actorRef2Scala
import imageAnalysis.model.BlurValue
import org.openimaj.image.processing.algorithm.FourierTransform

class BlurEstimateActor extends Actor {
  val threshold: Float = 2f;

  def receive = {
    case fImage: FImage => {
      val ft = new FourierTransform(fImage, false)
      val mag = ft.getMagnitude()

      var count = 0;
      for (y <- 0 to mag.height - 1) {
        for (x <- 0 to mag.width - 1) {
          if (Math.abs(mag.pixels(y)(x)) > threshold) {
            count = count + 1
          }
        }
      }
      val bpp: Double = count.doubleValue / (mag.height * mag.width).doubleValue

      sender ! BlurValue(bpp)
    }
    case _ => sender ! BlurValue
  }
}