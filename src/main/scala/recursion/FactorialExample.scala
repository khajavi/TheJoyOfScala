package recursion

import cats.data.State
import cats.{Monad, catsInstancesForId}

import scala.annotation.tailrec

object FactorialExample extends App {

  def fac1(n: Int): Int = {
    if (n == 0) 1 else n * fac1(n - 1)
  }

  def fac2(n: Int): Int = {
    @tailrec
    def go(n: Int, acc: Int): Int =
      if (n == 0) acc else go(n - 1, acc * n)
    go(n, 1)
  }

  def fac3(n: Int): Int = {
    def loop: State[Int, Int] =
      State.get.flatMap { x =>
        if (x == 1)
          State.pure(1)
        else
          State.set(x - 1).flatMap(_ => loop.map(z => x * z))
      }
    loop.run(n).value._2
  }
}

object MonadicRecursion extends App {
  def monadicTailRecFac[F[_]: Monad](n: Int): F[Int] =
    Monad[F].tailRecM((n, 1)) {
      case (n, acc) =>
        if (n == 0) Monad[F].pure(Right(acc))
        else if (true) Monad[F].pure(Left((n - 1, acc * n)))
        else Monad[F].pure(throw new Exception)
    }

  println(monadicTailRecFac(10)(catsInstancesForId))
  println(FactorialExample.fac1(10))

}
