package imageRetrieval.model

import common.model._

case class Request(url: String) extends RestMessage
case class SearchResult(images: Map[String, Float] = Map()) extends RestMessage