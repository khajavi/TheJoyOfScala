package cats.concurrent

import cats.effect.concurrent.Deferred
import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._

import scala.concurrent.ExecutionContext

object DeferredExample extends IOApp {
  // Needed for `start` or `Concurrent[IO]` and therefore `parSequence`
  implicit val cs = IO.contextShift(ExecutionContext.global)

  def start(d: Deferred[IO, Int]): IO[Unit] = {
    val attemptCompletion: Int => IO[Unit] = n => d.complete(n).attempt.void

    List(
      IO.race(attemptCompletion(1), attemptCompletion(2)),
      d.get.flatMap { n => IO(println(s"Result: $n")) }
    ).parSequence.void
  }

  val program: IO[Unit] =
    for {
      d <- Deferred[IO, Int]
      _ <- start(d)
    } yield ()


  override def run(args: List[String]): IO[ExitCode] = program.as(ExitCode.Success)
}
