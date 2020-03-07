package example.zio

import zio.ZIO

package object logger {
  def log(string: String): ZIO[Logger.Service, Nothing, Unit] =
    ZIO.accessM(_.log(string))
}
