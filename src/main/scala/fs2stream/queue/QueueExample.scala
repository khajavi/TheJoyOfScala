package fs2stream.queue

import cats.effect._
import fs2.concurrent.Queue

object QueueExample extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    for {
      x <- Queue.bounded[IO, Int](3)
      _ <- x.enqueue1(1)
      _ <- x.enqueue1(1)
      _ <- x.enqueue1(1)
      _ <- x.enqueue1(1)
      _ <- x.dequeue.evalTap(x => IO(println(x))).compile.drain
    } yield (ExitCode.Success)
  }

}

