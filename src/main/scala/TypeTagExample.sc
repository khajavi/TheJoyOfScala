val intList: List[Int] = List(1, 2, 3)
val strList: List[String] = List("foo", "bar")
import scala.reflect.runtime.universe._

trait Foo {

  type Bar

  def barType = weakTypeTag[Bar].tpe
}
import cats.implicits._

val a = (Some(2), Some(3)).tupled
val b = (IO(3), IO(35)).tupleLeft()

