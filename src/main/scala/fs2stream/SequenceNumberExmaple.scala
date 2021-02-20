package fs2stream

import cats.effect
import cats.effect.{ExitCode, IOApp}

object SequenceNumberExmaple extends IOApp {
  override def run(args: List[String]): effect.IO[ExitCode] = {
     val stream = for {
      s <- fs2.Stream(1,2,3,4,5,6)
      _ <- fs2.Stream.eval(effect.IO(println(s)))
    } yield ()
    
    stream.compile.drain.as(ExitCode.Success)
  }
}
