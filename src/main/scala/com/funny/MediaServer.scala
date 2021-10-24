package com.funny

import java.io.{File, PrintWriter, StringWriter}
import java.nio.file.Paths
import akka.actor.ActorSystem
import akka.http.scaladsl.server.RouteResult
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.{ContentType, HttpEntity, MediaType, MediaTypes, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{RequestContext, Route}
import akka.http.scaladsl.server.Directives.complete
import akka.stream.{ActorMaterializer, IOResult, Materializer}
import akka.stream.scaladsl.{FileIO, Sink, Source}
import akka.util.ByteString
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}


sealed trait Media {
  var mediaFile: Source[ByteString, Future[IOResult]]
  var mediaType: ContentType
}

object MediaServer extends Media with LazyLogging {
  private val conf: Configure = Configure()
  var mediaFile: Source[ByteString, Future[IOResult]] = null
  var mediaType: ContentType = null

  def setMediaFile(pathToFile: String = "/Users/kasper/Movies/BigBuckBunny.mp4"): Unit = {
    mediaFile = FileIO.fromPath(Paths.get(pathToFile))
    mediaType = MediaTypes.`video/mp4`.toContentType
    logger.info("Media File set")
  }

  def getMediaFile(): (ContentType, Source[ByteString, Future[IOResult]]) = {
    (mediaType, mediaFile)
  }

  private def displayMedia: RequestContext => Future[RouteResult] = path("mediaStream") {
    get {
      val data = getMediaFile()
      val entity = HttpEntity(data._1, data._2)
      println("About to return stream...")
      complete(entity)
    }
  }

  def loadMedia: RequestContext => Future[RouteResult] = path("loadMedia") {
    logger.info("Media Loaded")
    post{
      val pathToFile: String = "/Users/kasper/Movies/BigBuckBunny.mp4"
      setMediaFile(pathToFile)
      complete( StatusCodes.Accepted)
    }
  }

  def startMedia(caster: Caster): RequestContext => Future[RouteResult] = path("startMedia") {
    logger.info("Media Started")
    get {
      if(caster.loadMediaToCaster) {
        if(caster.playMediaOnCaster) complete( StatusCodes.Accepted)
        else complete(StatusCodes.BadRequest)
      } else {
        complete( StatusCodes.BadRequest)
      }
    }
  }

  def stopMedia(caster: Caster): RequestContext => Future[RouteResult] = path("stopMedia") {
    logger.info("Media Stopped")
    get {
      if(caster.stopMediaOnCaster) {
        complete( StatusCodes.Accepted)
      } else {
        complete( StatusCodes.BadRequest)
      }
    }
  }

  /**
   * Get Media server ip address from server.
   * @return String : ip address of media server.
   */
  def getMediaServerIpAddr(): String = {
    import java.net._
    val localhost: InetAddress = InetAddress.getLocalHost
    val localIpAddress: String = localhost.getHostAddress
    localIpAddress
  }


  def main(args: Array[String]) {
    val caster: Caster = new Caster()
    caster.initCaster
    lazy val route: Route = concat(displayMedia, startMedia(caster), stopMedia(caster), loadMedia)
    implicit val system = ActorSystem("mediaServerSystem")
    startServer(route)(system)
  }

  private def  startServer(routes: Route)(implicit system: ActorSystem): Unit = {
    implicit val ec: ExecutionContextExecutor = system.dispatcher
    logger.debug(s"Callback server about to start.")
    val futureBinding = Http().newServerAt(conf.mediaServerConf.serverBindAddr, conf.mediaServerConf.serverPort).bind(routes)
    futureBinding.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        logger.info(s"Server online at http://${address.getHostString}:${address.getPort}/")
      case Failure(ex) =>
        val sw = new StringWriter
        ex.printStackTrace(new PrintWriter(sw))
        logger.error(s"Failed to bind HTTP endpoint, terminating system. \n Exception: ${sw.toString}")
        system.terminate()
    }
  }
}