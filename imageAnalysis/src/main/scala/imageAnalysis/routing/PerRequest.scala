package imageAnalysis.routing

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
import imageAnalysis.routing.PerRequest._
import common.model._

trait PerRequest extends Actor with Json4sSupport {

  import context._

  //Json serialization
  val json4sFormats = DefaultFormats
  implicit val formats = Serialization.formats(NoTypeHints)

  def r: RequestContext
  def target: ActorRef
  def message: RestMessage

  //Actor timeout
  setReceiveTimeout(20.seconds)
  target ! message

  def receive = {
    //Analysis result
    case rest: RestMessage => {
      val entity = HttpEntity(`application/json`, pretty(render(parse(write(rest)))))
      r.responder ! HttpResponse(OK, entity)
      stopActor
    }
    //when actor times out
    case ReceiveTimeout => {
      r.complete(GatewayTimeout, Error("Request timeout"))
      stopActor
    }
    //When an exception is thrown
    case e: Throwable => {
      r.complete(BadRequest, Error(e.getMessage()))
      stopActor
    }
    //Unhandled message
    case x @ _ => {
      r.complete(BadRequest, Error("Internal server error" + x))
      stopActor
    }
  }

  //Stop the actor when request is completed
  def stopActor = {
    stop(self)
  }

  //when an unhandled exception is thrown
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