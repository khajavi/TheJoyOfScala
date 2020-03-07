package example.zio.logger

import zio.ZIO

object LiveLogger extends Logger.Service {
  override def log(str: String): ZIO[Any, Nothing, Unit] = {
    ZIO.effectTotal(println(str))
  }
}
