package fs2stream.topic

import fs2.Stream
import cats.effect.{ExitCode, IO, IOApp}
import fs2.concurrent.{Queue, Topic}

import scala.concurrent.duration._

/**
  * @author Milad Khajavi <khajavi@gmail.com>.
  */
object TopicExample extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    val app = for {
      t <- Stream.eval(Topic[IO, Long](0))
      res <- Stream.eval(t.subscribe(3).evalTap(x => IO(println(s"5 second: $x"))).compile.drain.start)
      _ <- Stream.eval(IO.sleep(1.seconds))
      res2 <- Stream.eval(t.subscribe(3).evalTap(x => IO(println(s"1 second: $x"))).compile.drain.start)
      x <-
        Stream.eval(t.publish(Stream.awakeEvery[IO](1.seconds).map(_._1)).compile.drain.start)
      _ <- Stream.eval(x.join)
      _ <- Stream.eval(res.join)
      _ <- Stream.eval(res2.join)
    } yield (res)

    app.compile.drain.as(ExitCode.Success)
  }
}
