package TypeClasses

import simulacrum._

@typeclass trait Semigroup[A] {
  @op("|+|") def append(x: A, y: A): A
}

object SemigroupImplicits {
  implicit val x: Semigroup[Int] = new Semigroup[Int] {
    override def append(x: Int, y: Int): Int = x + y
  }
}

@typeclass trait MyOrder[A] {
  @op("?!?") def compare(x: A, y: A): Int
}

object MyOrderImplicits {

  implicit object intInstance extends MyOrder[Int] {
    override def compare(x: Int, y: Int): Int =
      if (x < y) -1
      else if (x > y) +1
      else 0
  }

}
