package fs2stream.SignallingRef

import cats.effect
import cats.effect._
import fs2.concurrent.{Signal, SignallingRef}

import scala.concurrent.duration.{DurationDouble, DurationInt}

object SignalExample extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    val stream = for {
      x <- Signal.constant[IO, Int](1).discrete
      _ <- fs2.Stream.eval(IO(println(x)))
    } yield ()
    stream.compile.drain.as(ExitCode.Success)
  }
}

object SignallingRefExample extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    val stream = for {
      x <- fs2.Stream.eval(SignallingRef[IO, Int](1))
      y <-
        x.discrete concurrently fs2.Stream
          .eval(x.update(_ => (new java.util.Random).nextInt()))
          .evalTap(x => IO.sleep(4.seconds))
          .repeat
          _ <- x.discrete.evalTap(x => IO(println(x)))
//      _ <- fs2.Stream.eval(IO(println(y)))
    } yield ()
    stream.compile.drain.as(ExitCode.Success)
  }
}
