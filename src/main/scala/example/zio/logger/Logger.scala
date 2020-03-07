package example.zio.logger

import zio.{UIO, ZIO}


trait Logger {
  def logger: Logger.Service
}

object Logger {

  trait Service {
    def log(txt: String): UIO[Unit]
  }

}



