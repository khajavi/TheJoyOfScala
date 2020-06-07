package cats

import cats.effect.{ExitCode, IO, IOApp}
import fs2.concurrent.SignallingRef

object SignalExample extends IOApp {

  override def run(args: List[String]): effect.IO[ExitCode] = for {
    _ <- IO(3)
    s <- SignallingRef[IO, String]("init")
    s2 <- s.set("init2")
  } yield ExitCode.Success
}
