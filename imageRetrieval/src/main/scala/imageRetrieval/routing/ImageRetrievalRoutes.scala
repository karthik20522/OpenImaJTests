package imageRetrieval.routing

import akka.actor.{ Props, Actor, ActorRefFactory, ActorRef }
import spray.routing.{ Route, HttpService }
import imageRetrieval.model._
import imageRetrieval.routing.PerRequest._
import imageRetrieval.routing._
import imageRetrieval.actor.ImageRetrievalActor
import common.model._

class ImageRetrievalRoutes extends ImageRetrievalRT {

  implicit def actorRefFactory = context

  def receive = runRoute(imageRRoute)
}

/*Image Retrieval Routes*/
trait ImageRetrievalRT extends HttpService with Actor with PerRequestCreator {

  val imageRRoute = {
    /*
     * Given images are hosted on Pixibo
     * http://{host}/{image url}
     * */
    get {
      path(Rest) { (pathRest) =>
        processImageRoute {
          Request(pathRest)
        }
      }
    }
  }

  def processImageRoute(message: RestMessage): Route =
    ctx => perRequest(ctx, Props(new ImageRetrievalActor()), message)
}