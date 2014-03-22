package imageRetrieval.search

import net.semanticmetadata.lire
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.store.FSDirectory
import java.io.File
import net.semanticmetadata.lire.ImageSearcherFactory

object SearchIndex {
  /*
   * Get the index
   */
  def apply(name: String = "index") = name match {
    case _ => {
      DirectoryReader.open(FSDirectory.open(new File(name)));
    }
  }
}