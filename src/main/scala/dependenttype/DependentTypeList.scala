package dependenttype

import dependenttype.DependentTypeList.{MCons, MNil}

object Main extends App {
  val nums = MCons[Int](2, MCons(3, MNil()))
  val head: Int = nums.head
  val tailHead: Int = nums.tail.uncons.map(_.head).head
  println(tailHead - head)
}

object DependentTypeList {
  sealed abstract class MList { self =>
    type T
    def uncons: Option[MCons { type T = self.T }]
  }

  sealed abstract class MNil extends MList { self =>
    override def uncons: Option[MCons {type T = self.T}] = None
  }

  sealed abstract class MCons extends MList { self =>
    val head: T
    val tail: MList { type T = self.T }
    override def uncons: Option[MCons {type T = self.T}] = Some(self: MCons { type T = self.T })
  }

  def MNil[T0](): MNil { type T = T0 } =
    new MNil {
      type T = T0
    }

  def MCons[T0](hd: T0, tl: MList { type T = T0 }): MCons { type T = T0 } =
    new MCons {
      type T = T0
      override val head: T0 = hd
      override val tail: MList { type T = T0 } = tl
    }
}
