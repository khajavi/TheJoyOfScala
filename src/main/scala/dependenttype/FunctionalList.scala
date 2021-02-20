package dependenttype

import dependenttype.DependentTypeList.{MCons, MNil}

object FunctionalList extends App {
  val nums = MCons(2, MCons(3, MNil())): MCons{type T = Int}
  val head = nums.head
  val tailHead = nums.tail.uncons.map(_.head).head
  implicitly[Int <:< Int]
}


