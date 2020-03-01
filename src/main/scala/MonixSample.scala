import cats.effect.ExitCode
import monix.eval.{Task, TaskApp}

object MonixSample extends TaskApp {
  override def run(args: List[String]): Task[ExitCode] = {
    for {
      x <- Task(println("hello"))
    } yield ExitCode.Success
  }
}
