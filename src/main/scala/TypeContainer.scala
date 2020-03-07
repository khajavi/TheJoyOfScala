import java.util

import scala.collection.mutable.Set

object WrapJavaRawTypes extends App {
  val iter  : util.Set[_]      = (new Languages).contents
  val result: Existential[Set] = Existential.fromJavatoScala(iter)
  result.value.foreach(println)
}

trait Existential[Cont[_]] {
  type Elem
  type Container = Cont[Elem]
  val value: Container
}

case class ExistentialCont[C[_], T](value: C[T]) extends Existential[C] {
  override type Elem = T
  override type Container = C[T]
}

object Existential {
  def apply[C[_], T](value: C[T]): Existential[C] = ExistentialCont(value)

  def fromJavatoScala[T](javaSet: util.Set[T]): Existential[Set] = {
    val iter     = javaSet.iterator()
    val scalaSet = Set.empty[T]
    while (iter.hasNext)
      scalaSet += iter.next()

    Existential(scalaSet)
  }
}


