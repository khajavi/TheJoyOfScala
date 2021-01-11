package cats.concurrent

import cats.effect
import cats.effect.{Blocker, ExitCode, IO, IOApp}

object BlockerExample extends IOApp {
  def blockingOp: IO[Unit] = IO(Thread.sleep(3000)) *> IO(println("Hello"))

  def doSth(): IO[Unit] = IO(println("By By"))

  val prog = Blocker[IO].use { blocker =>
    for {
      _ <- blocker.blockOn(blockingOp) // executes on blocker, backed by cached thread pool
      _ <- doSth() // executes on contextShift
    } yield ()
  }

  override def run(args: List[String]): effect.IO[ExitCode] =
    prog.as(ExitCode.Success)
}
