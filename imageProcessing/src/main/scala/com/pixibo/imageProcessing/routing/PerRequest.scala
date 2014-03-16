package com.pixibo.imageProcessing.routing

import akka.actor._
import akka.actor.SupervisorStrategy.Stop
import spray.http.StatusCodes._
import spray.routing.RequestContext
import akka.actor.OneForOneStrategy
import spray.httpx.Json4sSupport
import scala.concurrent.duration._
import org.json4s.DefaultFormats
import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{ read, write }
import spray.http.StatusCodes._
import spray.http.MediaTypes.{ `image/jpeg`, `application/json` }
import spray.http.{ HttpEntity, HttpResponse, ContentType, MediaType }
import spray.routing.directives.RespondWithDirectives._
import com.pixibo.imageProcessing.routing.PerRequest._
import com.pixibo.imageProcessing.model._

trait PerRequest extends Actor with Json4sSupport {

  import context._

  /*Json serialization*/
  val json4sFormats = DefaultFormats
  implicit val formats = Serialization.formats(NoTypeHints)

  def r: RequestContext
  def target: ActorRef
  def message: RestMessage

  /*Actor timeout*/
  setReceiveTimeout(20.seconds)
  target ! message

  def receive = {
    /*When image byte array is received*/
    case img: Array[Byte] => {
      val entity = HttpEntity(`image/jpeg`, img)
      r.responder ! HttpResponse(OK, entity)
      stopActor
    }
    /*when actor times out*/
    case ReceiveTimeout => {
      println("\n\n\n TIMEOUT EXCEPTION \n\n\n")
      r.complete(GatewayTimeout, Error("Request timeout"))
      stopActor
    }
    /*When an exception is thrown*/
    case e: Throwable => {
      println("\n\n\n THROWABLE EXCEPTION \n\n\n")
      r.complete(BadRequest, Error(e.getMessage()))
      stopActor
    }

    case _ => {
      println("\n\n\n THROWABLE EXCEPTION \n\n\n")
      r.complete(BadRequest, Error("Internal server error"))
      stopActor
    }
  }

  /*Stop the actor when request is completed*/
  def stopActor = {
    stop(self)
  }

  /*when an unhandled exception is thrown*/
  override val supervisorStrategy =
    OneForOneStrategy() {
      case e => {
        r.complete(InternalServerError, Error("Internal Server Error"))
        Stop
      }
    }
}

object PerRequest {
  case class WithActorRef(r: RequestContext, target: ActorRef, message: RestMessage) extends PerRequest

  case class WithProps(r: RequestContext, props: Props, message: RestMessage) extends PerRequest {
    lazy val target = context.actorOf(props)
  }
}

trait PerRequestCreator {
  this: Actor =>

  def perRequest(r: RequestContext, target: ActorRef, message: RestMessage) =
    context.actorOf(Props(new WithActorRef(r, target, message)))

  def perRequest(r: RequestContext, props: Props, message: RestMessage) =
    context.actorOf(Props(new WithProps(r, props, message)))
}