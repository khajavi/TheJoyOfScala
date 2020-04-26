package cats

import cats.implicits._

object TraverseExample extends App {

  val numbers1                    = List(Some(-1), Some(2), None)
  val numbers2: List[Option[Int]] = List(Some(-1), Some(2))

  def biggerThanZero = (x: Option[Int]) => x match {
    case Some(value) if value > 0 => Some(value)
    case _ => None
  }

  println(
    numbers1.traverse(biggerThanZero),
    numbers1.traverse(identity),
  )

  println(
    numbers2.traverse(biggerThanZero),
    numbers2.traverse(identity)
  )
}
