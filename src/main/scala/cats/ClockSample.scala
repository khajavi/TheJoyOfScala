package cats

import java.util.concurrent.TimeUnit

import cats.effect.{Clock, ExitCode}
import monix.eval.{Task, TaskApp}

object ClockSample extends TaskApp{
  override def run(args: List[String]): Task[ExitCode] =
    for {
      t <- clock.realTime(TimeUnit.DAYS)
      _ <- Task(println(t))
    } yield (ExitCode.Success)


 val clock: Clock[Task] = Clock.create
  clock.realTime(TimeUnit.DAYS)

}



