package cats

import cats.effect.{ExitCode, IO, IOApp}

/**
 * @author Milad Khajavi <khajavi@gmail.com>
 */
object BracketExample extends IOApp {
  override def run(args: List[String]): effect.IO[ExitCode] = {
    val ab = IO.pure("Hello,")
      .bracket { x =>
        IO.pure(println(x + " World!"))
      } {
        x => IO.pure(println(s"releasing the $x resource"))
      }
    ab.flatMap(x => IO.pure(ExitCode.Success))
  }
}
