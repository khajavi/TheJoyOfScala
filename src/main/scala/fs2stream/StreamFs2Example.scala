package fs2stream

import cats.effect
import cats.effect._
import cats.effect.concurrent.Ref

/**
  * @author Milad Khajavi <khajavi@gmail.com>.
  */
object StreamFs2Example extends IOApp {

  import scala.concurrent.duration._
  import cats.implicits._
  import cats.syntax.all
  import cats.syntax
  import cats.data

  override def run(args: List[String]): IO[ExitCode] = {
    val counter = Counter()
    for {
      c <- counter
      _ <- c.inc
      _ <- c.inc
      _ <- c.inc
      f <- c.inc
      z <- c.get
      _ <- IO(println(z))
      _ <- IO(println(f))
    } yield (ExitCode.Success)
  }

  class Counter[F[_]](val state: Ref[F, Int]) {
    def inc: F[Unit] = state.update(x => x + 1)

    def get: F[Int] = state.get
  }

  object Counter {
    def apply(): IO[Counter[IO]] =
      for {
        r <- Ref.of[IO, Int](0)
      } yield new Counter(r)

  }

}

//object A extends App {
//  class Foo
//  class Bar extends Foo
//
//  implicit val a: Foo =:= Foo = new =:=[Foo, Foo] {
//    override def substituteBoth[F[_, _]](ftf: F[Foo, Foo]): F[Foo, Foo] = ftf
//  }
//
//  val res = implicitly[Foo =:= Foo]
//}
