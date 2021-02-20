package fs2stream

import cats.effect._
import fs2.Stream

import scala.concurrent.duration._

/**
 * @author Milad Khajavi <khajavi@gmail.com>.
 */
object ParJoinExample extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    val s: fs2.Stream[IO, fs2.Stream[IO, Long]] = for {
      a <- fs2.Stream(2,4,6,8).repeat.delayBy[IO](1.seconds)
      b <- fs2.Stream(1,2,3,4).repeat.delayBy[IO](1.seconds)
    } yield (Stream(a, b))
    s.parJoinUnbounded.evalTap(x => IO(println(x))).compile.drain.as(ExitCode.Success)
  }
}

object TestS extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    Stream(1,2,3).evalTap(x => IO(println(x))).compile.drain.as(ExitCode.Success)
  }
}
