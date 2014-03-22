package common.model

trait RestMessage
case class HttpException(msg: String) extends Exception(msg)
case class Error(message: String)
