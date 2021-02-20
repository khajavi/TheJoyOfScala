package cats.state

import cats.Monad

import scala.util.Random

object SeedGenerator1 extends App {
  type State[A, B] = A => (A, B)

  def randomInt: State[Long, Int] = { seed =>
    val rnd = new Random(seed)
    (rnd.nextLong(), rnd.nextInt())
  }

  val (s1, r1) = randomInt(1)
  val (s2, r2) = randomInt(s1)

  println(r1)
  println(r2)
}

case class State[A, B](run: A => (A, B)) {
  def map[C](f: B => C): State[A, C] =
    State { a =>
      val (a1, b) = run(a)
      (a1, f(b))
    }

  def flatMap[C](f: B => State[A, C]): State[A, C] =
    State { a =>
      val (a1, b) = run(a)
      f(b).run(a1)
    }
}

object SeedGenerator2 extends App {
//  type State[A, B] = A => (A, B)

  def randomInt: State[Long, Int] =
    State { seed =>
      val rnd = new Random(seed)
      (rnd.nextLong(), rnd.nextInt())
    }

  val randState = for {
    a <- randomInt
    b <- randomInt
    c <- randomInt
  } yield (a, b, c)

  val seed = 1L
  val (_, (a, b, c)) = randState.run(seed)
  println(a, b, c)
}
