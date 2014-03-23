package faceRecognition.face

import org.openimaj.image.FImage
import org.openimaj.image.processing.face.detection.CLMFaceDetector
import org.openimaj.image.processing.resize.ResizeProcessor
import scala.collection.JavaConverters._
import faceRecognition.model.Face
import faceRecognition.model.Position
import faceRecognition.model.DetectedFaces

class Detection {
  def detectFaces(fImage: FImage): DetectedFaces = {
    val clmfaces = getCLMFaces(fImage)
    val faces = clmfaces.map(f => Face(f.getConfidence(),
      Position(f.getBounds().getTopLeft().getX(), f.getBounds().getTopLeft().getY()),
      Position(f.getBounds().getBottomRight().getX(), f.getBounds().getBottomRight().getY()),
      f.getYaw(), f.getPitch(), f.getRoll()))

    DetectedFaces(clmfaces.size, faces = Some(faces.toList))
  }

  def getFaceStamps(fImage: FImage): List[FImage] = {
    val clmfaces = getCLMFaces(fImage)
    val faces = clmfaces.map(f => f.getFacePatch())
    faces.toList
  }

  private def getCLMFaces(fImage: FImage) = {
    val clm = new CLMFaceDetector()
    val resizedfImage = (fImage.getHeight(), fImage.getWidth()) match {
      case (h @ _, w @ _) if h > 1024 || w > 1024 => fImage.process(new ResizeProcessor(1024))
      case (_, _) => fImage
    }
    clm.detectFaces(resizedfImage).asScala
  }
}