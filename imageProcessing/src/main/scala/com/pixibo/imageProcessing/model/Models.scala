package com.pixibo.imageProcessing.model
import com.sksamuel.scrimage.Image

trait RestMessage
case class Error(message: String)
case class Request(id: String, restOperations: String) extends RestMessage
case class ProcessImage(image: Image, operations: Map[String, String])
case class HttpException(msg: String) extends Exception(msg)