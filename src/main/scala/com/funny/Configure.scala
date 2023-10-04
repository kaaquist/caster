package com.funny

import com.typesafe.scalalogging.LazyLogging
import pureconfig.generic.ProductHint
import pureconfig.{CamelCase, ConfigFieldMapping, SnakeCase}
import pureconfig.generic.auto._

case class Configure() extends LazyLogging {

  import Configure._

  implicit def hint[T]: ProductHint[T] = ProductHint[T](ConfigFieldMapping(CamelCase, SnakeCase))

  lazy val prefixRoot = "funny"

  lazy val mediaServerConf: MediaServerConf = pureconfig.loadConfigOrThrow[MediaServerConf](s"$prefixRoot.mediaServer")

}

object Configure {
  val theConf: Configure = Configure()

  case class MediaServerConf(
                              serverBindAddr: String,
                              serverPort: Int
                           )
}

