package cats

import cats.effect.{ExitCode, IOApp}

object ResourceExample extends IOApp {
  override def run(args: List[String]): effect.IO[ExitCode] =
    p.map(_ => ExitCode.Success)

  import cats.effect.{IO, Resource}

  val greet: String => IO[Unit] = x => IO(println("Hello, " ++ x))

  val p: IO[Unit] = Resource.liftF(IO.pure("World")).use(greet)

}
