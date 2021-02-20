package dependenttype

import scala.collection.mutable.ArrayBuffer

sealed abstract class TList {
  type T
  def uncons: Option[TCons]
}

case object TNil extends TList {
  override def uncons: Option[TCons] = None
}

abstract class TCons extends TList {
  def head: T
  def tail: TList
  override def uncons: Option[TCons] = Some(this: TCons)
}

object ListFactory {
  def TCons[T0](h: T0, t: TList): TCons =
    new TCons {
      override type T = T0
      override def head: T0 = h
      override def tail: TList = t
    }

  def mlength(xs: TList): Int = xs.uncons match {
    case Some(value) => 1 + mlength(value.tail)
    case None => 0
  }

  def copyToZero(xs: ArrayBuffer[_]): Unit =
    copyToZeroP(xs)

  def copyToZeroP[T](xs: ArrayBuffer[T]): Unit = xs += xs(0)
}

object ListExample extends App {
  import ListFactory._
  val list: TCons  = TCons[Int](1, TCons(1, TNil))
  val head: list.T = list.head
  val second: TCons#T = list.tail.uncons.map(_.head).head

  assert(head == second)
//  assert((head - second) == 0)
}
