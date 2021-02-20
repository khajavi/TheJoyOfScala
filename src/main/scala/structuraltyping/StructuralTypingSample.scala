package structuraltyping

import org.http4s.headers.Date
import scala.language.reflectiveCalls
object StructuralTypingSample extends App {

  trait Encoder[A] {
    def encode(a: A): String
  }

  val a: Encoder[{val foo: Int}] = new Encoder[{val foo: Int}] {
    override def encode(a: {
      val foo: Int
    }): String = a.foo.toString
  }

  case class Foo(val foo: Int)
 println(a.encode(Foo(2)))

  trait HasFoo {
    val foo: Int
  }

 implicit def fooEncoder[T <: HasFoo]: Encoder[T] = new Encoder[T] {
   override def encode(a: T): String = a.foo.toString
 }


  case class Bar(
      val i: Int,
      val b: String,
      val c: Double)

  case class Baz(
                  val i: Int,
                  val b: String,
                  val c: Date)

}
