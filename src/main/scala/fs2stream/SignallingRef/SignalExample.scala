package fs2stream.SignallingRef

import cats.effect
import cats.effect.{ExitCode, IOApp}
import fs2.concurrent.{Signal, SignallingRef}

import scala.concurrent.duration.{DurationDouble, DurationInt}

object SignalExample extends IOApp {
  override def run(args: List[String]): effect.IO[ExitCode] = {
    val stream = for {
      x <- Signal.constant[effect.IO, Int](1).discrete
      _ <- fs2.Stream.eval(effect.IO(println(x)))
    } yield ()
    stream.compile.drain.as(ExitCode.Success)
  }
}

object SignallingRefExample extends IOApp {
  override def run(args: List[String]): effect.IO[ExitCode] = {
    val stream = for {
      x <- fs2.Stream.eval(SignallingRef[effect.IO, Int](1))
      y <-
        x.discrete concurrently fs2.Stream
          .eval(x.update(_ => (new java.util.Random).nextInt()))
          .evalTap(x => effect.IO.sleep(3.seconds))
          .repeat
      _ <- fs2.Stream.eval(effect.IO(println(y)))
    } yield ()
    stream.compile.drain.as(ExitCode.Success)
  }
}
