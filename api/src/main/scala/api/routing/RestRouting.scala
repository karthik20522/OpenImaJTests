package api.routing

import akka.actor._
import spray.routing.HttpService
import spray.http._
import spray.routing._
import api.routing._

class RestRouting extends HttpService with Actor with PerRequestCreator {
  implicit def actorRefFactory = context

  def receive = ???
}
