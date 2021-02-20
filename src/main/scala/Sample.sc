import cats.Order
import cats.data.Kleisli
import cats.implicits.{catsContravariantMonoidalForOrder, catsKernelStdOrderForInt, _}
import fs2.Pure

case class Foo(v: Int)

implicit val fooOrdInstance: Order[Foo] =
  Order[Int].contramap(_.v)

trait Last[Tuple] {
  type Tail

  def apply(): Tuple => Tail
}

object Last {
  type Aux[In, Out] = Last[In] {type Tail = Out}

  implicit def tuple2instance[A, B]: Last[(A, B)] {type Tail = B} = new Last[(A, B)] {
    override type Tail = B

    override def apply(): ((A, B)) => B = _._2
  }

}


def sort[MyTuple, MyTail](xs: List[MyTuple])(implicit last: Last.Aux[MyTuple, MyTail], ord: Ordering[MyTail]) = {
  def f: MyTuple => MyTail = last()

  xs.sortBy(f)
}

sort[(Int, Int), Int](List((1, 5), (3, 4)))


trait Unwrap[T[_], R] {
  type Out

  def apply(tr: T[R]): Out
}

object Unwrap {

  implicit object listIntInstance extends Unwrap[List, Int] {
    override type Out = String

    override def apply(tr: List[Int]) =
      tr.mkString(", ")
  }

}

def foo(xs: List[Int])(implicit unwrap: Unwrap[List, Int]) = {
  unwrap(xs)
}


foo(List(1, 2, 3, 4))


val a: fs2.Stream[Pure, Int] = fs2.Stream(1, 2, 3)

a.compile.drain


case class QueueName(value: String) extends AnyVal

object QueueName {
  implicit val queueNameOrder: Order[QueueName] = Order.by(_.value)
}

val q = QueueName("folan")
val b = QueueName("olan")
Order.gt(q, b)




val intList: List[Int] = List(1, 2, 3)
val strList: List[String] = List("foo", "bar")

def checkType[A](xs: List[A]) = xs match {
  case _: List[String] => "List of Strings"
  case _: List[Int] => "List of Ints"
}