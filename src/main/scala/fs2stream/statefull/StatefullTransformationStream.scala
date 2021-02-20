package fs2stream.statefull

import cats.effect.{ExitCode, IO, IOApp}
import fs2.{Pipe, Pull}

object StatefullTransformationStream extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    fs2.Stream.fromIterator[IO]((1 to 1000).iterator).evalTap(x => IO(println(s"x: $x"))).rechunkRandomly()
      .through(tk(30))
      .evalTap(x => IO(println(x)))
      .compile
      .drain
      .as(ExitCode.Success)
  }

  def take[F[_], I](n: Long): Pipe[F, I, I] =
    in => in.scanChunksOpt(n) { n =>
      if (n <= 0) None
      else
        Some(c => c.size match {
          case m if m < n => {
            println("1. m, n", m, n)
            (n - m, c)
          }
          case m => {
            println("2. m, n", m, n)
            (0, c.take(n.toInt))
          }
        })
    }

  def tk[F[_], I](n: Long): Pipe[F, I, I] = {
    def go(s: fs2.Stream[F, I], n: Long): Pull[F, I, Unit] = {
      s.pull.uncons.flatMap {
        case Some((hd, tl)) =>
          hd.size match {
            case m if m <= n => Pull.output(hd) >> go(tl, n - m)
            case m => Pull.output(hd.take(n.toInt)) >> Pull.done
          }
        case None =>
          Pull.done
      }
    }

    in => go(in, n).stream
  }
}
