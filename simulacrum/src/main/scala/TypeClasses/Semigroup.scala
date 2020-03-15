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
