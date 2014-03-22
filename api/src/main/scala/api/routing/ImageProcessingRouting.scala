package api.routing

import spray.routing.HttpService

trait ImageProcessingRouting extends HttpService {

  val processingRoutes = {
    path(Segment / Rest) { (id, pathRest) =>
      complete {
        "OK"
      }
    }
  }
}