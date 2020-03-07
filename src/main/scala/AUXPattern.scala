import shapeless.ops.hlist.Length

object AUXPattern1 {

  trait Foo[A] {
    type B

    def value: B
  }

  implicit def fi = new Foo[Int] {
    type B = String

    def value: B = "Foo"
  }

  implicit def fs = new Foo[String] {
    type B = Boolean

    def value: B = false
  }

  def foo[T](t: T)(implicit f: Foo[T]): f.B = f.value
}


object Main1 extends App {

  import AUXPattern1._

  val res1: String  = foo(2)
  val res2: Boolean = foo("")

  println(res1)
  println(res2)
}

object AUXPattern2 {

  trait Foo[A] {
    type B

    def value: B
  }

  // Doesn't compile
  //illegal dependent method type: parameter appears in the type of another parameter in the same section or an earlier one
  //def foo[T](t:T)(implicit f:Foo[T], m: Monoid[f.B] ): f.B = m.zero
}

object AUXPattern3 extends App {

  import scalaz._
  import Scalaz._

  trait Foo[A] {
    type B

    def value: B
  }

  object Foo {
    type Aux[A0, B0] = Foo[A0] {type B = B0}
  }

  implicit def fi = new Foo[Int] {
    type B = String

    def value: B = "Foo"
  }

  implicit def fs = new Foo[String] {
    type B = Boolean

    def value: B = false
  }

  implicit val booleanMonoid = new Monoid[Boolean] {
    override def zero: Boolean = true

    override def append(f1: Boolean, f2: => Boolean): Boolean = f1 || f2
  }

  def foo[T, R](t: T)(implicit f: Foo.Aux[T, R], m: Monoid[R]): R = m.zero

  val res1: String  = foo(2)
  val res2: Boolean = foo("")

  println(res1, res2)
}

object AUXPattern4 extends App {

  import shapeless._

  def length[T, R <: HList](t: T)(implicit g: Generic.Aux[T, R],
                                  l: Length[R]): l.Out = l()

  case class Foo(i:Int , s: String, b: Boolean)
  val foo = Foo(1, "3", true)

  println(length(foo))
}
