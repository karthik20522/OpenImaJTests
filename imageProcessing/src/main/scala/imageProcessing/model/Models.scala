package imageProcessing.model
import com.sksamuel.scrimage.Image
import common.model._

case class ProcessImage(image: Image, operations: Map[String, String])
case class Request(id: String, restOperations: String) extends RestMessage