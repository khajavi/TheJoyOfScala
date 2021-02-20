package subspace

import shapeless.test.illTyped

object SubspaceExample extends App {
  implicitly[TrueType =:= TrueType]
  implicitly[FalseType =:= FalseType]
  illTyped("implicitly[TrueType =:= FalseType]")
  illTyped("implicitly[FalseType =:= TrueType]")
  implicitly[TrueType#Not =:= FalseType]
  implicitly[FalseType#Not =:= TrueType]
  illTyped("implicitly[TrueType#Not =:= TrueType]")
  illTyped("implicitly[FalseType#Not =:= FalseType]")

}

sealed trait BoolVal {
  def not: BoolVal
  def or(that: BoolVal): BoolVal
}
case object TrueVal extends BoolVal {
  override def not: BoolVal = FalseVal
  override def or(that: BoolVal): BoolVal = TrueVal
}
case object FalseVal extends BoolVal {
  override def not: BoolVal = TrueVal
  override def or(that: BoolVal): BoolVal = that
}

sealed trait BoolType {
  type Not <: BoolType
  type Or[That <: BoolType] <: BoolType
}
sealed trait TrueType extends BoolType {
  override type Not = FalseType
  override type Or[That <: BoolType] = TrueType
}
sealed trait FalseType extends BoolType {
  override type Not = TrueType
  override type Or[That <: BoolType] = That
}

sealed trait IntVal {
  def plus(that: IntVal): IntVal
}

case object Int0 extends IntVal {
  override def plus(that: IntVal): IntVal = that
}

case class IntN(prev: IntVal) extends IntVal {
  override def plus(that: IntVal): IntVal =
    IntN(prev plus that)
}

object IntValExample extends App {
  val int1 = IntN(Int0)
  val int2 = IntN(int1)
  val int3 = IntN(int2)
}

object Try1 {
  sealed trait IntType {
    type Plus[That <: IntType] <: IntType
  }
  sealed trait Int0Type extends IntType {
    override type Plus[That <: IntType] = That
  }
  sealed trait IntNType[Prev <: IntType] extends IntType {
    override type Plus[That <: IntType] = IntNType[Prev#Plus[That]]
  }

  sealed trait IntList {
    def ::(head: Int): IntList = IntListImpl(head, this)
    def ++(that: IntList): IntList
    def sum(that: IntList): IntList
    def size: Int
  }

  case object IntNil extends IntList {
    override def size: Int = 0

    override def ++(that: IntList): IntList = that

    override def sum(that: IntList): IntList = {
      require(that == IntNil)
      this
    }
  }

  case class IntListImpl(head: Int, tail: IntList) extends IntList {
    override def size: Int = 1 + tail.size

    override def ++(that: IntList): IntList = head :: tail ++ that

    override def sum(that: IntList): IntList = {
      require(that.size == size)
      that match {
        case IntNil            => IntNil
        case IntListImpl(h, t) => (h + head) :: (tail sum t)
      }
    }
  }

  object IntTypeExamle extends App {
    type Int1 = IntNType[Int0Type]
    type Int2 = IntNType[Int1]
    type Int3 = IntNType[Int2]

    implicitly[Int0Type#Plus[Int1] =:= Int1]
    implicitly[Int1#Plus[Int1] =:= Int2]
    implicitly[Int2#Plus[Int1] =:= Int3]
  }
  object IntListExample extends App {
    val sum = (1 :: 2 :: 3 :: IntNil) sum (4 :: 5 :: 6 :: IntNil)
    println(sum)

    // Throw exception
    //  val sum2 = (1 :: 2 :: 3 :: IntNil) sum (4 :: 5 :: 6 :: IntNil)
  }
}

object Try3 extends App {

sealed trait IntType { type Plus[That <: IntType] <: IntType }
sealed trait IntZero extends IntType { type Plus[That <: IntType] = That }
sealed trait IntN[Prev <: IntType] extends IntType {
  type Plus[That <: IntType] = IntN[Prev#Plus[That]]
}

sealed trait IntList[Size <: IntType] {
  def ::(head: Int): IntList[IntN[Size]] = IntListImpl(head, this)
  def ++[ThatSize <: IntType](that: IntList[ThatSize]): IntList[Size#Plus[ThatSize]]
  def +(that: IntList[Size]): IntList[Size]
  def size: Int
}

  case object IntNil extends IntList[IntZero] {
    override def size: Int = 0
    override def ++[ThatSize <: IntType](
        that: IntList[ThatSize]
    ): IntList[ThatSize] = that
    override def +(that: IntList[IntZero]): IntList[IntZero] = {
      require(that == IntNil)
      this
    }
  }

  case class IntListImpl[TailSize <: IntType](
      head: Int,
      tail: IntList[TailSize]
  ) extends IntList[IntN[TailSize]] {
    override def size: Int = 1 + tail.size

    override def ++[ThatSize <: IntType](
        that: IntList[ThatSize]
    ): IntList[IntN[TailSize#Plus[ThatSize]]] =
      head :: (tail ++ that)

    override def +(
        that: IntList[IntN[TailSize]]
    ): IntList[IntN[TailSize]] = {
      require(that.size == size)
      that match {
        case IntListImpl(h, t) => (h + head) :: (tail + t)
      }
    }

  }

  object IntTypeExamle extends App {
    type Int1 = IntN[IntZero]
    type Int2 = IntN[Int1]
    type Int3 = IntN[Int2]

    implicitly[IntZero#Plus[Int1] =:= Int1]
    implicitly[Int1#Plus[Int1] =:= Int2]
    implicitly[Int2#Plus[Int1] =:= Int3]
  }

  val sum = (1 :: 2 :: 3 :: IntNil) + (4 :: 5 :: 6 :: IntNil)
  println(sum)

  val list = (1 :: 2 :: 3 :: IntNil) ++ (4 :: 5 :: 6 :: IntNil)
  println(list)

  // Throw exception
  //  val sum2 = (1 :: 2 :: 3 :: IntNil) sum (4 :: 5 :: 6 :: IntNil)
}
