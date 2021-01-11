package cats.concurrent

import cats.effect.{Async, ExitCode, IO, IOApp}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object AsyncExample extends IOApp {
  val apiCall = Future.successful("I come from the Future!")

  val ioa: IO[String] =
    Async[IO].async { (cb: Either[Throwable, String] => Unit) =>
      import scala.util.{Failure, Success}

      apiCall.onComplete {
        case Success(value) => cb(Right(value))
        case Failure(error) => cb(Left(error))
      }
    }

  override def run(args: List[String]): IO[ExitCode] = ioa.flatMap(x => IO(println(x)).as(ExitCode.Success))
}
