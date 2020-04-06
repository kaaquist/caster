package com.funny

import java.io.{PrintWriter, StringWriter}

import com.typesafe.scalalogging.LazyLogging
import su.litvak.chromecast.api.v2.{ChromeCast, ChromeCasts, Status}

class Caster extends LazyLogging {
  private val conf = Configure()
  private var deviceIsLive: Boolean = false
  private var chromeCast: ChromeCast = null
  private var status: Status = null

  def initCaster: Unit = {
    logger.info("initCaster called")
    ChromeCasts.startDiscovery
    // Not to the boolean => false become true.
    while (!deviceIsLive) {
      try {
        //TODO: Here we just take the first device that gets found on the network. That might not be the device we need.
        chromeCast = ChromeCasts.get.get(0)
        //ChromeCasts.get().forEach(println)
        deviceIsLive = true
        // Connect (optional)
        // Needed only when 'autoReconnect' is 'false'.
        // Usually not needed and connection will be established automatically.
        // chromeCast.connect();
        // Get device status
        status = chromeCast.getStatus
        logger.debug(s"This here is the chromecast found: ${chromeCast.getTitle}.\n" +
          s"Status for stand by for the chromecast: ${status.standBy}")
      } catch {
        case e: java.lang.IndexOutOfBoundsException =>
          //This here is ok since that is what is expected when we are discovering the chromecast.
          logger.debug(e.getMessage)
        case ex: Throwable =>
          val sw = new StringWriter
          ex.printStackTrace(new PrintWriter(sw))
          logger.error(s"An error occurred: ${sw.toString}")
          throw new Exception("Error in the hole ")
      }
    }
  }

  def loadMediaToCaster: Boolean = {
    //TODO: The app id is for the Default Media Receiver :: https://developers.google.com/cast/v2/receiver_apps#default
    logger.info("loaded Media to Caster")
    try {
      chromeCast.launchApp("CC1AD845")
      chromeCast.load(s"http://${MediaServer.getMediaServerIpAddr()}:${conf.mediaServerConf.serverPort}/mediaStream")
      true
    } catch {
      case ex: Exception =>
        val sw = new StringWriter
        ex.printStackTrace(new PrintWriter(sw))
        logger.error(s"An error occurred: ${sw.toString}")
        false
    }
  }

  def playMediaOnCaster: Boolean = {
    try {
      logger.debug("Before PLAY ")
      chromeCast.play()
      logger.debug("After PLAY ")
      true
    } catch {
      case ex: Exception =>
        val sw = new StringWriter
        ex.printStackTrace(new PrintWriter(sw))
        logger.error(s"An error occurred: ${sw.toString}")
        false
    }
  }

  def stopMediaOnCaster: Boolean = {
    try {
      chromeCast.stopApp()
      true
    } catch {
      case ex: Exception =>
        val sw = new StringWriter
        ex.printStackTrace(new PrintWriter(sw))
        logger.error(s"An error occurred: ${sw.toString}")
        false
    }
  }
}