package com.pixibo.imageAnalysis.routing

import akka.actor.{ Props, Actor, ActorRefFactory, ActorRef }
import spray.routing.{ Route, HttpService }
import com.pixibo.imageAnalysis.model._
import com.pixibo.imageAnalysis.routing.PerRequest._
import com.pixibo.imageAnalysis.routing._
import com.pixibo.imageAnalysis.actor._

class ImageAnalysisRoutes extends ImageAnalysisRT {

  implicit def actorRefFactory = context

  def receive = runRoute(imageARoute)

}

/*Image Analysis Routes*/
trait ImageAnalysisRT extends HttpService with Actor with PerRequestCreator {

  val blurEstimatorActor = context.actorOf(Props[BlurEstimateActor], "Blur")
  val sharpnessEstimatorActor = context.actorOf(Props[SharpnessEstimateActor], "Sharpness")
  val colorEstimatorActor = context.actorOf(Props[ColorEstimateActor], "Color")
  val naturalnessEstimatorActor = context.actorOf(Props[NaturalnessEstimateActor], "Naturanlness")
  val faceEstimatorActor = context.actorOf(Props[FaceDetectorActor], "FaceDetector")
  val metadataExtractActor = context.actorOf(Props[MetadataActor], "Metadata")
  val contrastEstimatorActor = context.actorOf(Props[ContrastEstimateActor], "Contrast")

  val testImage = "http://ppcdn.500px.org/60292350/bc290ee7408c1d11a469e0565775b8f1ba5697a2/5.jpg"

  val imageARoute = {
    /*
     * Given images are hosted on Pixibo
     * http://{host}/{imageId}/{ImageProcessing Operations}
     * */
    get {
      path("analyse" / Rest) { (rest) =>
        analyseImageRoute {
          RequestAnalysis(rest)
        }
      }
    }
  }

  def analyseImageRoute(message: RestMessage): Route =
    ctx => perRequest(ctx, Props(new ImageAnalysisActor(blurEstimatorActor,
      sharpnessEstimatorActor,
      colorEstimatorActor,
      naturalnessEstimatorActor,
      faceEstimatorActor,
      metadataExtractActor,
      contrastEstimatorActor)), message)
}