package imageRetrieval.actor

import akka.actor.Actor
import imageRetrieval.model._
import imageRetrieval.search._
import net.semanticmetadata.lire.ImageSearcherFactory
import common.helper.Utils
import java.net.ConnectException
import scala.concurrent.Future
import dispatch.Defaults.executor
import dispatch._
import akka.actor.OneForOneStrategy
import akka.actor.SupervisorStrategy.Escalate
import scala.collection.JavaConverters._
import net.semanticmetadata.lire.DocumentBuilder
import common.model.HttpException

class ImageRetrievalActor extends Actor {
  var images: Map[String, Float] = Map()
  def receive = {
    case Request(uri) => {
      Utils.downloadFileFromURL(uri) match {
        case Right(res) => {
          val indexReader = SearchIndex()
          val searcher = ImageSearcherFactory.createFCTHImageSearcher(10)
          val hits = searcher.search(res.getResponseBodyAsStream(), indexReader)
          for (i <- 0 until hits.length()) {
            val fileName = hits.doc(i).getValues(DocumentBuilder.FIELD_NAME_IDENTIFIER)(0)
            images += fileName -> hits.score(i)
          }
          context.parent ! SearchResult(images)
        }
        case Left(_: ConnectException) => context.parent ! HttpException("Could not connect to server")
        case Left(StatusCode(404)) => context.parent ! HttpException("Image Not Found")
        case Left(StatusCode(code)) => context.parent ! HttpException("Failed to download Image" + code.toString)
        case Left(e) => context.parent ! HttpException("Failed to download Image. " + e.getMessage())
      }
    }
  }

  override val supervisorStrategy =
    OneForOneStrategy() {
      case _ => Escalate
    }
}
