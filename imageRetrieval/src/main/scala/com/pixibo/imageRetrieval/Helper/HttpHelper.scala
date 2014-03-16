package com.pixibo.imageRetrieval.Helper

import dispatch._
import dispatch.Defaults.executor
import scala.concurrent.Future
import com.ning.http.client.Response

object Utils {

  val dispatchHttp = new dispatch.Http {
    import com.ning.http.client._
    val builder = new AsyncHttpClientConfig.Builder()
    builder.setCompressionEnabled(true)
      .setAllowPoolingConnection(true)
      .setAllowSslConnectionPool(true)
      .setFollowRedirects(false)
      .setRequestTimeoutInMs(5000)
    override val client = new AsyncHttpClient(builder.build())
  }

  def downloadFileFromURL(uri: String): Either[Throwable, Response] = {
    val req = url(uri).GET
    dispatchHttp(req).either()
  }
}