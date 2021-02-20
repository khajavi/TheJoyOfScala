package fs2stream

import java.util.concurrent.Executors

import cats.effect._
import fs2._

import scala.concurrent.ExecutionContext
import scala.util.Random

object ConcurrentComputation extends App {

  val ec = ExecutionContext.global
  implicit val contextShift: ContextShift[IO] = IO.contextShift(ec)
  //  override def run(args: List[String]): IO[ExitCode] = {
  val stream = fs2.Stream
    .range[IO](1, Int.MaxValue, 1)
    .map(x =>
      Stream
        .emit[IO, Int](x)
        .evalMap(x =>
          IO {
            Thread.sleep(1000);
            val rand = new Random()
            val n    = rand.nextInt(900000000)
            ((n % 17) == 0, n)
          }
        )
    )
    .parJoin(32)
    .evalTap(x => IO(println(x)))
    .find(_._1 == true)
    .evalTap(x => IO(println(s"res: ${x}")))

  println(
    stream.compile
      .fold(Option.empty[Int]) {
        case (_, (b, r)) =>
          if (b) Some(r) else None
      }
      .unsafeRunSync()
  )
  //    stream.compile.drain.as(ExitCode.Success)
}

object ABGD extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    fs2.Stream
      .iterate(0L)(_ + 1)
      .covary[IO]
      .evalTap(x =>
        IO {
          println(x);
          Thread.sleep(500)
        }
      )
      .map(x => fs2.Stream.emit(x).covary[IO])
      .parJoin(5)
      .find(_ == 5L)
      .compile
      .drain
      .as(ExitCode.Success)
  }
}

object H extends App {
  val ec = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(8))
  implicit val contextShift: ContextShift[IO] = IO.contextShift(ec)

  val res = fs2.Stream
    .iterate(0L)(_ + 1)
    .covary[IO]
//    .debug()
    .map(x => Stream.eval(
       IO {
          Thread.sleep(1000)
          println(s"inner: $x")
          x
        }
      )
    )
    .parJoin(5)
    .find(_ == 40L)
    .compile
    .fold(Option.empty[Long]) { case (_, b) => Some(b) }
    .unsafeRunSync()
    .get

  println(res)
}
