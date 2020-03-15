import TypeClasses.Semigroup
import TypeClasses.SemigroupImplicits._

object A extends App {
  import Semigroup.ops._
  println(1 |+| 2)
}