package imageProcessing.routing

import akka.actor.{ Props, Actor, ActorRefFactory, ActorRef }
import spray.routing.{ Route, HttpService }
import imageProcessing.model._
import imageProcessing.routing.PerRequest._
import imageProcessing.routing._
import imageProcessing.actor.ImageProcessingActor
import common.model._

class ImageProcessingRoutes extends ImageProcessingRT {

  implicit def actorRefFactory = context

  def receive = runRoute(imagePRoute)

}

/*Image Processing Routes*/
trait ImageProcessingRT extends HttpService with Actor with PerRequestCreator {

  val testImage = "http://ppcdn.500px.org/60292350/bc290ee7408c1d11a469e0565775b8f1ba5697a2/5.jpg"

  val imagePRoute = {
    /*
     * Given images are hosted on Pixibo
     * http://{host}/{imageId}/{ImageProcessing Operations}
     * */
    get {
      path(Segment / Rest) { (id, pathRest) =>
        processImageRoute {
          Request(testImage, pathRest)
        }
      }
    }
  }

  def processImageRoute(message: RestMessage): Route =
    ctx => perRequest(ctx, Props(new ImageProcessingActor()), message)
}