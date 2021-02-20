package dependenttype

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object TypeExtractor extends App {

  class Graph {
    val nodes: List[Node] = Nil

    class Node {
      val connected: List[Node] = Nil
      def connectTo(node: Node): List[Node] = {
        if (connected.exists(node.equals)) connected
        else connected.appended(node)
      }
    }
  }

  trait Input {
    type Output
    val value: Output
  }

  def dependentFunc(i: Input): i.Output = i.value

  def valueOf[T](v: T) =
    new Input {
      type Output = T
      val value: T = v
    }

  val intValue = valueOf(1)
  val stringValue = valueOf("One")

  assert(dependentFunc(intValue) == 1)
  assert(dependentFunc(stringValue) == "One")

//val f1 = new Foo
//val b1: f1.Bar = new f1.Bar
//val f2 = new Foo
//val b2: f2.Bar = new f2.Bar

}

trait Inner[F] {
  type T

  def apply(f: F): Either[String, T]
}

object Inner {
  def apply[F](implicit isf: Inner[F]) = isf

  implicit def mk[A] =
    new Inner[Option[A]] {
      type T = A

      def apply(f: Option[A]): Either[String, T] = f match {
        case Some(value) => Right(value)
        case None => Left("not exists")
      }
    }
}

object ABCED extends App {

  def logResult[Thing](thing: Thing)(implicit i: Inner[Thing]): Either[String, i.T] =
    i(thing)

  logResult(Option(32))

}
