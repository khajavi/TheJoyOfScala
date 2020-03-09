package example.zio

import zio.{App, ZIO}

object ZioSample extends App {
  val program: ZIO[Logger.Service with Console.Service, Nothing, Int] = {
    console.putStrLn("hello") *>
      logger.log("Hello, from example.zio.logger!") *>
      ZIO.succeed(1)
  }

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] = {
    program.provide(new Logger.Live with Console.Live)
  }
}
