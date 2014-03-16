package com.pixbo.api.model

trait RestMessage
case class Request(id: String, restOperations: String) extends RestMessage
case class HttpException(msg: String) extends Exception(msg)
case class Error(message: String)

/*METADATA*/
case class RequestMetadata(uri: String) extends RestMessage

/*IMAGE ANALYSIS*/
case class RequestAnalysis(uri: String) extends RestMessage