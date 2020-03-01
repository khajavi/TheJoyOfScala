import cats.effect.ExitCode
import monix.eval.{Task, TaskApp}

object MonixSample extends TaskApp {
  override def run(args: List[String]): Task[ExitCode] = {
    for {
      x <- Task(println("hello"))
      y <- Task(Task.raiseError(new Exception("fatal error")))
      z <- y.attempt
      _ = z match {
        case Left(value) =>
          println(s"error: $value")
        case Right(value) =>
          println("application closed without problem.")
      }
    } yield ExitCode.Success
  }
}
