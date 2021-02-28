package example.zio

import zio.{ExitCode, URIO, ZIO}

object ZioSample extends zio.App {
  val program: ZIO[Logger.Service with Console.Service, Throwable, Int] = {
    console.putStrLn("hello") *>
      logger.log("Hello, from example.zio.logger!") *>
      ZIO.succeed(1)
  }

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
    program.provide(new Logger.Live with Console.Live).exitCode
  }
}
