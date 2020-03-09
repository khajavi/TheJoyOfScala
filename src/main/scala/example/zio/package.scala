package example.zio

import zio.ZIO

package object logger {
  def log(string: String): ZIO[Logger.Service with Console.Service, Nothing, Unit] =
    ZIO.accessM(_.log(string))
}

package object console {
  def putStrLn(str: String): ZIO[Console.Service with Logger.Service, Nothing, Unit] =
    ZIO.accessM(_.putStrLn(str))
}