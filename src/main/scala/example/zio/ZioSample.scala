package example.zio

import example.zio.logger.{LiveLogger, Logger}
import zio.ZIO
import zio.App

object ZioSample extends App {
  val program: ZIO[Logger.Service, Nothing, Int] = {
    //    console.putStrLn("hello") *>
    logger.log("Hello, from example.zio.logger!") *>
      ZIO.succeed(1)
  }

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] = {
    program.provideSome(???)
  }
}
