package cats

import cats.effect.concurrent.Ref
import cats.effect.{ExitCode, IO, IOApp}

object RefExample extends IOApp {

  val data = Ref[IO].of(4)

  override def run(args: List[String]): IO[ExitCode] = {
    for {
      s <- data
      access <- s.access
      (value, setter) = access
      _ <- IO(println(value))
      _ <- (setter(7))
      _ <- (setter(8)) //doesn't work
      j <- s.get
      _ <- IO(println(j))
    } yield ExitCode.Success
  }
}

object F extends App {
  val decider = Decider(_.toString)
}

object Decider {
  type Decider = Int => String

  def apply(f: Int => String): Decider = f
}
