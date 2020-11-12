package cats

import cats.effect.{ExitCode, IOApp, Sync, Timer}

import scala.concurrent.duration.DurationInt

object TimerExample1 extends IOApp {
  override def run(args: List[String]): effect.IO[ExitCode] =
    for {
      _ <- Sync[effect.IO].delay(println("hello"))
      _ <- Timer[effect.IO].sleep(2.seconds)
      _ <- Sync[effect.IO].delay(println("World!"))
    } yield ExitCode.Success
}

object TimerExample2 extends IOApp {
  override def run(args: List[String]): effect.IO[ExitCode] =
    Sync[effect.IO].delay(println("hello")) *>
      Timer[effect.IO].sleep(2.seconds) *>
      Sync[effect.IO].delay(println("World!")).as(ExitCode.Success)
}
