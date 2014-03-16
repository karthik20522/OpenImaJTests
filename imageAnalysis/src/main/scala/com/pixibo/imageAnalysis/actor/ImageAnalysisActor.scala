package com.pixibo.imageAnalysis.actor

import akka.actor.Actor
import scala.concurrent.Future
import dispatch._
import dispatch.Defaults.executor
import akka.actor.OneForOneStrategy
import akka.actor.SupervisorStrategy.Escalate
import org.openimaj.image.{ MBFImage, ImageUtilities }
import java.io.ByteArrayInputStream
import java.net.ConnectException
import akka.actor.ActorRef
import scala.util.Try
import com.pixibo.imageAnalysis.model._
import scala._
import org.openimaj.image.processing.resize.ResizeProcessor
import org.joda.time.{ DateTime, Interval }
import com.typesafe.scalalogging.slf4j.Logging
import com.pixibo.imageAnalysis.Helper.Utils
import org.openimaj.image.processing.face.detection.DetectedFace

class ImageAnalysisActor(blurEstimator: ActorRef,
  sharpnessEstimator: ActorRef,
  colorEstimator: ActorRef,
  naturalEstimator: ActorRef,
  faceDetection: ActorRef,
  metadataExtractor: ActorRef,
  contrastEstimator: ActorRef) extends Actor with Logging {

  var blurValue = Option.empty[BlurValue]
  var sharpnessValue = Option.empty[SharpnessValue]
  var colorValue = Option.empty[ColorValue]
  var naturalnessValue = Option.empty[NaturalnessValue]
  var contrastValue = Option.empty[ContrastValue]
  var faceDetected = Option.empty[DetectedFaces]
  var metadata = Option.empty[ProcessedMetadata]
  var currentDateTime = Option.empty[DateTime]
  var downloadTime = Option.empty[String]
  val startDateTime = DateTime.now()

  def receive = {
    case RequestAnalysis(uri) => {
      val startTime = DateTime.now
      Utils.downloadFileFromURL(uri) match {
        case Right(res) => {

          val interval = new Interval(startTime, DateTime.now());
          downloadTime = Some((interval.toDurationMillis() / 1000) + "s." + (interval.toDurationMillis() % 1000) + "ms")

          res.getStatusCode() match {
            case 200 => {
              val responseBytes = res.getResponseBodyAsBytes
              val inputStream = new ByteArrayInputStream(responseBytes)
              try {
                val originalImage = ImageUtilities.readMBF(inputStream)
                val mbfImage = (originalImage.getHeight(), originalImage.getWidth()) match {
                  case (h @ _, w @ _) if h > 640 || w > 640 => originalImage.process(new ResizeProcessor(320))
                  case (_, _) => originalImage
                }
                val fImage = mbfImage.flatten() //grayscale image
                currentDateTime = Some(DateTime.now)

                blurEstimator ! fImage //gray image
                sharpnessEstimator ! fImage //gray image
                colorEstimator ! mbfImage //color image
                naturalEstimator ! mbfImage //color image
                contrastEstimator ! mbfImage //color image
                faceDetection ! originalImage.flatten() //gray image
                metadataExtractor ! responseBytes //image bytes

                context.become(waitingResponses)
              } finally {
                Try(inputStream.close)
              }
            }
            case _ => context.parent ! HttpException("Image Not Found at " + uri)
          }

        }
        case Left(_: ConnectException) => context.parent ! HttpException("Could not connect to server")
        case Left(StatusCode(404)) | Left(StatusCode(400)) => context.parent ! HttpException("Image Not Found at " + uri)
        case Left(StatusCode(408)) => context.parent ! HttpException("Failed to download Image. Request timed out.")
        case Left(StatusCode(code)) => context.parent ! HttpException("Failed to download Image" + code.toString)
        case Left(e) => context.parent ! HttpException("Failed to download Image. " + e.getMessage())
      }
    }
  }

  def getTimeTaken: Option[String] = {
    val interval = new Interval(currentDateTime.get, DateTime.now);
    Some((interval.toDurationMillis() / 1000) + "s." + (interval.toDurationMillis() % 1000) + "ms")
  }

  def waitingResponses: Receive = {
    case blur: BlurValue => {
      blurValue = Some(BlurValue(value = blur.value, timeTaken = getTimeTaken))
      replyIfReady
    }
    case sharpness: SharpnessValue => {
      sharpnessValue = Some(SharpnessValue(value = sharpness.value, timeTaken = getTimeTaken))
      replyIfReady
    }
    case color: ColorValue => {
      colorValue = Some(ColorValue(value = color.value, timeTaken = getTimeTaken))
      replyIfReady
    }
    case naturalness: NaturalnessValue => {
      val qValue = naturalness.value match {
        case _ if naturalness.value == 0.0 => 0.15
        case _ => naturalness.value
      }
      naturalnessValue = Some(NaturalnessValue(value = qValue, timeTaken = getTimeTaken))
      replyIfReady
    }
    case contrast: ContrastValue => {
      contrastValue = Some(ContrastValue(value = contrast.value, timeTaken = getTimeTaken))
      replyIfReady
    }
    case face: DetectedFaces => {
      faceDetected = Some(DetectedFaces(hits = face.hits, timeTaken = getTimeTaken, faces = face.faces))
      replyIfReady
    }
    case meta: ProcessedMetadata => {
      metadata = Some(ProcessedMetadata(meta.meta, getTimeTaken))
      replyIfReady
    }
  }

  def replyIfReady = if (blurValue.nonEmpty && contrastValue.nonEmpty && sharpnessValue.nonEmpty && colorValue.nonEmpty && naturalnessValue.nonEmpty && faceDetected.nonEmpty && metadata.nonEmpty) {

    val imageRank = (blurValue.get.value + sharpnessValue.get.value + colorValue.get.value + naturalnessValue.get.value) match {
      case t if t >= 2 => 1
      case t if t >= 1 && t < 2 => 2
      case _ => 3
    }
    val interval = new Interval(startDateTime, DateTime.now());
    val analysisResult = AnalysisResult(e = Some(Map("totalTime" -> ((interval.toDurationMillis() / 1000) + "s." + (interval.toDurationMillis() % 1000) + "ms"),
      "fileDownloadTime" -> downloadTime.get,
      "blur" -> blurValue.get.timeTaken.get,
      "sharpness" -> sharpnessValue.get.timeTaken.get,
      "color" -> colorValue.get.timeTaken.get,
      "quality" -> naturalnessValue.get.timeTaken.get,
      "contrast" -> contrastValue.get.timeTaken.get,
      "faceDetection" -> faceDetected.get.timeTaken.get,
      "metadata" -> metadata.get.timeTaken.get)),
      imageRank = imageRank,
      blur = blurValue.get.value,
      sharpness = sharpnessValue.get.value,
      color = colorValue.get.value,
      quality = naturalnessValue.get.value,
      contrast = contrastValue.get.value,
      faces = DetectedFaces(hits = faceDetected.get.hits, faces = faceDetected.get.faces),
      metadata = metadata.get.meta)

    context.parent ! analysisResult
  }

  override val supervisorStrategy =
    OneForOneStrategy() {
      case _ => Escalate
    }
}