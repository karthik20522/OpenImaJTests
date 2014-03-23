package faceRecognition.face

import org.openimaj.data.dataset.MapBackedDataset
import org.openimaj.data.dataset.ListDataset
import org.openimaj.image.FImage
import java.io.File
import java.io.FilenameFilter
import org.openimaj.image.ImageUtilities
import org.openimaj.image.MBFImage
import org.openimaj.image.colour.Transforms
import org.openimaj.data.dataset.ListBackedDataset
import org.openimaj.data.dataset.GroupedDataset
import org.openimaj.image.processing.face.detection.keypoints.FKEFaceDetector
import org.openimaj.image.processing.face.alignment.RotateScaleAligner
import org.openimaj.image.processing.face.recognition.EigenFaceRecogniser
import org.openimaj.feature.DoubleFVComparison
import org.openimaj.image.processing.face.recognition.FaceRecognitionEngine
import org.openimaj.image.processing.face.recognition.FaceRecogniser
import scala.collection.JavaConverters._

class Training {

  /*
   * Train facerecognition engine
   * */
  def createAndTrainRecognitionEngine(dataset: GroupedDataset[String, ListDataset[FImage], FImage], numberOfComponents: Integer = 15, threshold: Float = 8f, kNearestNeighbors: Integer = 5) = {
    val faceDetector = new FKEFaceDetector(100)
    val faceAligner = new RotateScaleAligner()
    val recogniser: FaceRecogniser[org.openimaj.image.processing.face.detection.keypoints.KEDetectedFace, String] = EigenFaceRecogniser.create(numberOfComponents, faceAligner, kNearestNeighbors, DoubleFVComparison.EUCLIDEAN, threshold);
    val engine = FaceRecognitionEngine.create(faceDetector, recogniser);
    engine.train(dataset)
    engine
  }

  /*
   * - Load images from directory provided
   * - Convert to grayscale image and return a list of images
   * */
  def loadImages(path: String): MapBackedDataset[String, ListDataset[FImage], FImage] = {
    var groupedDataset = new MapBackedDataset[String, ListDataset[FImage], FImage]();
    val folder = new File(path)
    val jpgfiles = folder.listFiles().filter(_.toString().toLowerCase.endsWith(".jpg"))
    var list = new ListBackedDataset[FImage]()

    for (file <- jpgfiles) {
      val loadedImage = loadImage(file.getAbsolutePath());
      val trainingImage = Transforms.calculateIntensity(loadedImage);
      list.add(trainingImage);
    }

    groupedDataset.getMap().put(folder.getName(), list)
    groupedDataset
  }

  /*Load file to MBF format*/
  def loadImage(filename: String): MBFImage = {
    val image = ImageUtilities.readMBF(new File(filename));
    return image;
  }
}