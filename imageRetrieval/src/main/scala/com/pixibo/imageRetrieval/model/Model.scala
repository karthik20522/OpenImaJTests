package com.pixibo.imageRetrieval.model

trait RestMessage
case class Error(message: String)
case class Request(url: String) extends RestMessage
case class HttpException(msg: String) extends Exception(msg)
case class SearchResult(images: Map[String, Float] = Map()) extends RestMessage