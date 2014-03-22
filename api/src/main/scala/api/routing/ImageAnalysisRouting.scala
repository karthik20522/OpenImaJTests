package api.routing

import spray.routing.HttpService

trait ImageAnalysisRouting extends HttpService {

  val analysisRoute = {
    path("analyse" / Rest) { (rest) =>
      complete {
        "OK"
      }
    }
  }
}