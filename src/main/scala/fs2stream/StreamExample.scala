package fs2stream

import cats.effect.concurrent.Deferred
import cats.effect.{Concurrent, ContextShift, ExitCode, IO, IOApp, Sync}
import cats.kernel.Monoid
import fs2._
import fs2.concurrent.Queue

import scala.concurrent.ExecutionContext

object StreamExample extends App {
  val ec: ExecutionContext = ExecutionContext.Implicits.global
  implicit lazy val ioContextShift: ContextShift[IO] = IO.contextShift(ec)

  val stream =
    fs2
      .Stream
      .bracket(IO {
        println("started");
        4
      })(x => IO(println("finished" + x)))
      .flatMap(_ => fs2.Stream(Seq(1, 2, 3, 5, 5, 77)))
      .compile
      .drain
      .unsafeRunSync()

  val s1 = Stream(1, 0).repeat.take(6).toList
  val s2 = Stream(1, 2, 3).drain.toList
  val s3 = Stream
    .eval_(IO(println("!!")))
    .compile
    .toVector
    .unsafeRunSync()

  //  val s4 = (Stream(1, 2) ++ Stream(3).map(_ => throw new Exception("Noooo")) ++ Stream(4, 5, 6))
  //    .attempt
  //    .toList.foreach(println)


  def tk[F[_], O](n: Long): Pipe[F, O, O] =
    in => in.scanChunksOpt[Long, O, O](n) { state: Long =>
      if (state <= 0) None
      else Some(c => c.size match {
        case m if m < n => (n - m, c)
        case m => (0, c.take(n.toInt))
      })
    }

  val s5 = Stream(1, 2, 3, 4, 5, 6, 7).through(tk(3)).toList
  println(s5)


  val s6 =
    Stream(1, 2, 3)
      .merge(Stream.eval(IO {
        Thread.sleep(3000);
        4
      }).merge(Stream.eval(IO {
        Thread.sleep(2000);
        5
      })))
      .compile
      .toVector
      .unsafeRunSync()
  println(s6)
}

object InterruptionExample extends App {
  implicit val contextShift = IO.contextShift(ExecutionContext.global)
  implicit val timer        = IO.timer(ExecutionContext.global)

  import scala.concurrent.duration._

  val program =
    Stream.eval(Deferred[IO, Unit]).flatMap { switch =>
      val switcher =
        Stream.eval(switch.complete(())).delayBy(5.seconds)

      val program =
        Stream.repeatEval(IO(println(java.time.LocalTime.now()))).metered(1.seconds)

      program
        .interruptWhen(switch.get.attempt)
        .concurrently(switcher)
    }

  program.compile.drain.unsafeRunSync()
}

object SynchronousEffects extends App {
  def destroyUniverse(): Unit = {
    println("Booom!!!")
  }

  val s   = Stream.eval_(IO(destroyUniverse())) ++
    Stream("moving on")
  val res = s.compile.toVector.unsafeRunSync()
  println(res)

  println("##############")

  val T    = Sync[IO]
  val s2   = Stream.eval_(T.delay(destroyUniverse())) ++
    Stream("... moving on")
  val res2 = s2.compile.toVector.unsafeRunSync()
  println(res2)
}

object AsynchronousEffects extends App {

  trait Connection {
    def readBytes(onSuccess: Array[Byte] => Unit, onFailure: Throwable => Unit): Unit

    def readBytesE(onComplete: Either[Throwable, Array[Byte]] => Unit): Unit =
      readBytes(bs => onComplete(Right(bs)), e => onComplete(Left(e)))

    override def toString = "<connection>"
  }

  val c = new Connection {
    override def readBytes(onSuccess: Array[Byte] => Unit, onFailure: Throwable => Unit): Unit = {
      Thread.sleep(200)
      onSuccess(Array(0, 1, 3))
    }
  }

  val bytes: IO[Array[Byte]] = cats.effect.Async[IO].async[Array[Byte]](c.readBytesE)

  val res = Stream.eval(bytes).map(_.toList).compile.toVector.unsafeRunSync()
  println(res)
}

object NothingExample extends App {
  def oneOrThrow(num: Int): Int =
    if (num == 1) num: Int
    else (throw new Exception()): Nothing

  val res: Int = oneOrThrow(2)
  println(res)

  val int   : Int    = throw new Exception("fake Int")
  val string: String = throw new Exception("fake String")

  case class User()

  val maybeUser: Option[User] = throw new Exception("fake User")

  def equalsOrFail[A](l: A, r: A): A =
    if (l == r) l
    else throw new Exception(s"$l is not $r")


  sealed abstract class Optional[+A]

  final case class Maybe[A](value: A) extends Optional[A]

  final case object None extends Optional[Nothing]

}

object CovaryExample extends App {
  val stream = Stream(1, 2, 3).covary[IO]
}

object StreamInterruption extends App {

  import cats.effect.IO
  import fs2._

  case object Err extends Throwable

  val s1: List[Int] = (Stream(1) ++ Stream(2).map(_ => throw Err)).take(1).toList
  println(s1)

  val s2: List[Int] = (Stream(1) ++ Stream.raiseError[IO](Err)).take(1).compile.toList.unsafeRunSync()
  println(s2)

  val s3: List[Int] = Stream(1).covary[IO]
    .onFinalize(IO(println("finilizing")))
    .compile
    .toList
    .unsafeRunSync()

  println(s3)

}

object MergeExample extends App {

  case object Err extends Throwable

  implicit lazy val ioContextShift: ContextShift[IO] =
    IO.contextShift(ExecutionContext.global)
  val s1 = (Stream(1) ++ Stream(2)).covary[IO]
  val s2 = (Stream.empty ++
    Stream.raiseError[IO](Err))
    .handleErrorWith { e =>
      println(s"error: $e");
      Stream.raiseError[IO](e)
    }

  val merged = s1 merge s2 take 1
  println(
    merged.compile.toList.unsafeRunSync()
  )
}

object FIFOExample extends IOApp {

  import cats.implicits._
  import fs2.concurrent.Queue

  class Buffering[F[_]](q1: Queue[F, Int], q2: Queue[F, Int])(implicit val C: Concurrent[F]) {
    def start: Stream[F, Unit] =
      q2.dequeue.evalMap(n =>
        C.delay(println(s"pulling out $n from Queue #2"))
      ) concurrently Stream.range(0, 10)
        .covary[F]
        .through(q2.enqueue)
  }

  override def run(args: List[String]): IO[ExitCode] = {
    val stream = for {
      q1 <- Stream.eval(Queue.bounded[IO, Int](1)).onFinalize(IO(println("hahaha")))
      q2 <- Stream.eval(Queue.bounded[IO, Int](100)).onFinalize(IO(println("q2 finilized")))
      bp = new Buffering[IO](q1, q2)
      _ <- bp.start.drain
    } yield ()
    stream.compile.drain.as(ExitCode.Success)
  }
}

object StreamTakeDropWhile extends App {
  val s  = Stream.range(1, 10)
  val s1 = s.dropWhile(_ != 5).toList
  println(s1)
  val s2 = s.dropThrough(_ != 5).toList
  println(s2)
  val s3 = s.takeWhile(_ != 5).toList
  println(s3)
  val s4 = s.takeThrough(_ != 5).toList
  println(s4)
}


object ScanExample extends App {

  import cats.implicits._

  val s1 = Stream.range(1, 4)
  println(s1.compile.toList)
  val s2 = s1.scan(2)(_ + _)
  println(s2.compile.toList)
  val s3 = s1.fold(2)(_ + _)
  println(s3.compile.toList)
  val s4 = s1.foldMap(x => x)
  println(s4.compile.toList)
  val s5 = s1.scan1(_ + _)
  println(s5.compile.toList)
  val s6 = s1.scanMap(x => x)
  println(s6.compile.toList)
  val s7 = s1.map(x => x).scanMonoid
  println(s7.compile.toList)
  implicit val M = implicitly[Monoid[Int]]
  val s8 = s1.scan(M.empty)(M.combine)
  println(s8.compile.toList)
}

object Buffering extends IOApp {
  val point: IO[Long] = IO(System.currentTimeMillis() / 2000)

  import scala.concurrent.duration._

  val s: Stream[IO, Long] = Stream.awakeEvery[IO](1.seconds).evalMap(_ => point)

  def buffer(stream: Stream[IO, Long], queue: Queue[IO, Long]): Stream[IO, Unit] = for {
    h <- stream.head
    _ <- Stream.eval(IO(println(s"head: $h")))
    _ <- Stream.eval(queue.enqueue1(h))
    _ <- stream.tail.evalMap(queue.enqueue1)
  } yield ()

  val result: Stream[IO, Long] = for {
    q <- Stream.eval(Queue.bounded[IO, Long](100))
    _ <- Stream.eval(buffer(s, q).compile.drain.start)
    p <- Stream.eval(point).delayBy(4.seconds)
    i <- q.dequeue.dropWhile(_ != p)
  } yield i

  override def run(args: List[String]): IO[ExitCode] = {
    result.evalMap(x => IO(println(x))).compile.drain.map(_ => ExitCode.Success)
  }
}