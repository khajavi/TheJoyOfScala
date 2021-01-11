package cats.concurrent

import cats.effect.concurrent.Ref
import cats.effect.{Concurrent, ConcurrentEffect, ExitCode, IO, IOApp, Sync, Timer, _}
import cats.implicits._

import scala.concurrent.duration._

object ConcurrentExample extends IOApp {
  val F = Concurrent[IO]
  val T = Timer[IO]

  val tick: IO[Unit] = F.uncancelable(timer.sleep(10.seconds))

  val res = for {
    fiber <- F.start(tick)
    _ <- fiber.cancel
    _ <- fiber.join
    _ <- F.delay {
      println("Tick!")
    }
  } yield ()

  override def run(args: List[String]): IO[ExitCode] = res.map(_ => ExitCode.Success)
}


class NumberSubscribe[F[_] : Timer : Sync : ConcurrentEffect]
(
  status: Ref[F, Fiber[F, Unit]]
) {

  import cats.effect._
  import cats.effect.syntax.all._

  def printEvens: fs2.Stream[F, FiniteDuration] =
    fs2.Stream.awakeEvery[F](1.second).filter(_._1 % 2 == 0).evalTap(x => Sync[F].delay(println(x)))

  def print: F[Unit] =
    for {
      f <- printEvens.compile.drain.start
      _ <- status.set(f)
    } yield ()

  def join: F[Unit] =
    for {
      res <- status.get
      _ <- res.join
    } yield ()


}

object NumberSubscribe {
  def apply[F[_] : Timer : ConcurrentEffect](): Resource[F, NumberSubscribe[F]] =
    Resource.make {
      for {
        x <- Ref.of(Fiber[F, Unit](Sync[F].unit, Sync[F].unit))
      } yield
      new NumberSubscribe[F](x)
    } { n =>
      println("releasing")
      n.join
    }
}

object MyMain extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    val s = NumberSubscribe[IO]()
    s.use { s =>
      for {
        _ <- s.print
      } yield ()
    }
  }.as(ExitCode.Success)
}
