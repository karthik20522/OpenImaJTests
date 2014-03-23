package faceRecognition.model

case class Face(confidence: Float, topLeft: Position, bottomRight: Position, yaw: Double, pitch: Double, roll: Double)
case class DetectedFaces(hits: Int = 0, timeTaken: Option[String] = None, faces: Option[List[Face]] = None)
case class Position(x: Float, y: Float)