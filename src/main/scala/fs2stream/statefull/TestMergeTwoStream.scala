package fs2stream.statefull

import cats.effect.{ExitCode, IO, IOApp}

import scala.language.postfixOps

object TestMergeTwoStream extends IOApp {
  import fs2._

  override def run(args: List[String]): IO[ExitCode] = {
    def firstStream(to: Int): Stream[IO, Int] = fs2.Stream.range(1, to).covary[IO]

    def secondStream: Stream[IO, Int] = fs2.Stream.range(6, 20) //.evalTap(x => IO(println(s"x: $x")))

    def append(stream: => Int => Stream[IO, Int]): Pipe[IO, Int, Int] = {
      def go(s: Stream[IO, Int], firstTry: Boolean): Pull[IO, Int, Unit] = {
        for {
          r <- s.pull.uncons.flatMap {
            case Some((hd, tl)) if firstTry =>
              val head = hd.toList.head
              for {
                l <- Pull.eval(stream(head).compile.toList)
                r <- Pull.output(Chunk.array[Int](l appendedAll hd.toList toArray)) >> go(tl, firstTry = false)
              } yield (r)
            case Some((hd, tl)) =>
              Pull.output(hd) >> go(tl, firstTry = false)
            case None =>
              Pull.done
          }
        } yield (r)
      }

      s => go(s, firstTry = true).stream
    }

    secondStream.through(append(firstStream)).evalTap(x => IO(println(x))).compile.drain.as(ExitCode.Success)
  }

}

