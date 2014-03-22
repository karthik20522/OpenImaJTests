package imageAnalysis.actor

import akka.actor.{ ActorRef, Actor, OneForOneStrategy }
import akka.actor.SupervisorStrategy.Escalate
import scala.Some
import scala.concurrent.Future
import com.drew.imaging.ImageMetadataReader
import java.io.{ ByteArrayInputStream, FileOutputStream, File }
import com.github.nscala_time.time.Imports._
import scala.collection.JavaConverters._
import java.io.BufferedInputStream
import java.net.ConnectException
import dispatch.StatusCode
import imageAnalysis.model._
import scala.util.Try
import common.helper._
import common.model.HttpException

/**
 * *
 * EXTRACT METADATA FROM IMAGE - EXIF, IPTC
 */
class MetadataActor() extends Actor {
  def receive = {
    case image: Array[Byte] => {
      sender ! extractMetadata(image)
    }
    case RequestMetadata(uri) =>
      {
        Utils.downloadFileFromURL(uri) match {
          case Right(res) => context.parent ! extractMetadata(res.getResponseBodyAsBytes)
          case Left(_: ConnectException) => context.parent ! HttpException("Could not connect to server")
          case Left(StatusCode(404)) => context.parent ! HttpException("Image Not Found")
          case Left(StatusCode(code)) => context.parent ! HttpException("Failed to download Image" + code.toString)
          case Left(e) => context.parent ! HttpException("Failed to download Image. " + e.getMessage())
        }
      }
    case _ => context.parent ! new Exception("Unknown message")
  }

  /**
   * *
   * Extract Metadata from Image
   * @param imageBytes byte array of an image
   */
  def extractMetadata(imageBytes: Array[Byte]): ProcessedMetadata = {
    val inputStream = new ByteArrayInputStream(imageBytes)
    val bufferedinputStream = new BufferedInputStream(inputStream)
    var meta = Map[String, String]()
    try {
      val metadata = ImageMetadataReader.readMetadata(bufferedinputStream, false);
      for (directory <- metadata.getDirectories().asScala) {
        for (tag <- directory.getTags().asScala) {
          if (!tag.getTagName().contains("TRC"))
            meta += (tag.getTagName() -> tag.getDescription())
        }
      }
    } finally {
      Try(bufferedinputStream.close)
      Try(inputStream.close)
    }
    ProcessedMetadata(meta)
  }

  override val supervisorStrategy =
    OneForOneStrategy() {
      case _ => Escalate
    }
}