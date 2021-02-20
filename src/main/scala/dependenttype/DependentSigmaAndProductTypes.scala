package dependenttype

import scala.annotation.implicitNotFound

/**
  * @author Milad Khajavi <khajavi@gmail.com>.
  */
object DependentSigmaAndProductTypes extends App {

  class Foo { class Bar }
  class Baz extends Foo

  val foo1 = new Foo
  val foo2 = new Foo

  implicitly[=:=[Foo, Foo]]
//  implicitly[foo1.Bar =:= foo2.Bar]

  trait Sigma {
    val foo: Foo
    val bar: foo.Bar
  }

  val sigma = new Sigma {
    override val foo: Foo = foo1
    override val bar: foo.Bar = new foo.Bar
  }

  trait Pi[T] { type U }

  def depList[T](t: T)(implicit pi: Pi[T]): List[pi.U] = Nil

  object Foo
  object Bar

  implicit val fooInt = new Pi[Foo.type] { type U = Int }
  implicit val barString = new Pi[Bar.type] { type U = String }

  val res1 = depList(Foo)
  val res2 = depList(Bar)

  implicitly[res2.type <:< List[String]]

}
