package com.funny

import java.io.File
import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.{ContentType, HttpEntity, MediaType, MediaTypes, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives.complete
import akka.stream.{ActorMaterializer, IOResult, Materializer}
import akka.stream.scaladsl.{FileIO, Sink, Source}
import akka.util.ByteString
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, ExecutionContextExecutor, Future}


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

  private def displayMedia = path("mediaStream") {
    get {
      val data = getMediaFile()
      val entity = HttpEntity(data._1, data._2)
      println("About to return stream...")
      complete(entity)
    }
  }

  def loadMedia = path("loadMedia") {
    logger.info("Media Loaded")
    post{
      val pathToFile: String = "/Users/kasper/Movies/BigBuckBunny.mp4"
      setMediaFile(pathToFile)
      complete( StatusCodes.Accepted)
    }
  }

  def startMedia(caster: Caster) = path("startMedia") {
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

  def stopMedia(caster: Caster) = path("stopMedia") {
    logger.info("Media Started")
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
    implicit val system = ActorSystem("mediaServerSystem")
    implicit val materializer = ActorMaterializer()

    lazy val route: Route = displayMedia ~ startMedia(caster) ~ stopMedia(caster) ~ loadMedia

    val serverSource = Http().bind(interface = conf.mediaServerConf.serverBindAddr, port = conf.mediaServerConf.serverPort)
    val bindingFuture = serverSource.to(Sink.foreach { connection ⇒
      logger.info("Accepted new connection from " + connection.remoteAddress)
      connection handleWith route
    }).run()
    logger.info(s"Server online at http://${conf.mediaServerConf.serverBindAddr}:${conf.mediaServerConf.serverPort}/\n " +
      s"Press RETURN to stop...")
    scala.io.StdIn.readLine()
    bindingFuture.flatMap(_.unbind()).onComplete(_ => system.terminate())
  }
}