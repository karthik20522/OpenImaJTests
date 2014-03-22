package imageAnalysis.model

import common.model._

case class ContrastValue(value: Double = 0, timeTaken: Option[String] = None)
case class BlurValue(value: Double = 0, timeTaken: Option[String] = None)
case class SharpnessValue(value: Double = 0, timeTaken: Option[String] = None)
case class ColorValue(value: Double = 0, timeTaken: Option[String] = None)
case class NaturalnessValue(value: Double = 0, timeTaken: Option[String] = None)

case class Face(confidence: Float, topLeft: Position, bottomRight: Position, yaw: Double, pitch: Double, roll: Double)
case class DetectedFaces(hits: Int = 0, timeTaken: Option[String] = None, faces: Option[List[Face]] = None)
case class Position(x: Float, y: Float)

case class ProcessedMetadata(meta: Map[String, String] = Map(), timeTaken: Option[String] = None) extends RestMessage

/*METADATA*/
case class RequestMetadata(uri: String) extends RestMessage

/*IMAGE ANALYSIS*/
case class RequestAnalysis(uri: String) extends RestMessage
case class AnalysisResult(
  imageRank: Int = 1,
  blur: Double = 0,
  sharpness: Double = 0,
  color: Double = 0,
  quality: Double = 0,
  contrast: Double = 0,
  faces: DetectedFaces,
  metadata: Map[String, String] = Map(),
  e: Option[Map[String, String]] = None)
  extends RestMessage

/*IMAGE FEATURE EXTRACTOR*/
case class RequestFeatureExtract(uri: String) extends RestMessage