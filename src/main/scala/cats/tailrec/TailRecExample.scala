package cats.tailrec

import cats.effect.{ExitCode, IOApp, Sync}
import cats.implicits._
import cats.{Monad, effect}

object TailRecExample extends IOApp {
  override def run(args: List[String]): effect.IO[ExitCode] = {
//    effect.IO(println(sum[Id](List(1, 2, 3)))).as(ExitCode.Success)
    rec2[effect.IO](5).flatMap(x => effect.IO(println(x))).as(ExitCode.Success)
  }

  def sum[F[_]](numbers: List[F[Int]])(implicit m: Monad[F]): F[Long] =
    m.tailRecM((numbers, 0)) {
      case (lst, acc) =>
        lst match {
          case Nil =>
            m.pure(Right(acc))
          case ::(head, tail) =>
            m.map(head)(h => Left(tail, acc + h))
        }
    }

  def list[F[_]: Sync]: F[List[Int]] = Sync[F].delay(List(1, 2, 3, 4))

  def rec[F[_]: Sync](n: Int): F[Int] = {
    Sync[F].tailRecM((n, 0)) {
      case (n, acc) =>
        if (n == 0) Sync[F].pure(Right(acc))
        else list.map(_.sum).map(x => Left(n - 1, acc + x))
    }
  }

  def rec2[F[_]: Sync](n: Int): F[Int] = {
    Sync[F].tailRecM((n, 0)) {
      case (n, acc) =>
        for {
          l <- list
          head = l.head
          r <- head match {
            case 2 => Sync[F].pure(Right(acc))
            case _ =>
              list
                .map(_.sum)
                .map(x => if (n != 0) Left(n - 1, acc + x) else Right(acc))
          }
        } yield (r)
    }
  }
}
