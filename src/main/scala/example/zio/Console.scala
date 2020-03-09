package example.zio

import zio.{UIO, ZIO}

trait Console {
  def console: Console.Service
}

object Console {
  trait Service {
    def putStrLn(line: String): UIO[Unit]
  }

  trait Live extends Service {
    override def putStrLn(line: String): UIO[Unit] =
      ZIO.effectTotal(println(line))
  }

  object Live extends Live
}
