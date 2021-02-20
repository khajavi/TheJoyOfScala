package dependenttype

object ComparatorExample1 extends App {

  trait Comparator[T] {
    def ordering: Ordering[T]

    def compare(a: T, b: T): Int = ordering.compare(a, b)
  }


  implicit object IntComparator extends Comparator[Int] {
    override def ordering: Ordering[Int] = Ordering.Int
  }

  def max[T: Comparator](items: Seq[T]): T = {
    val comp = implicitly[Comparator[T]]
    items match {
      case a :: b :: tail =>
        val m = if (comp.compare(a, b) > 0) a else b
        max(m :: tail)
      case a :: Nil => a
      case Nil => throw new Exception
    }
  }

  println(max(Seq(1, 5, 3, 9, 4)))
}


object ComparatorExample2 extends App {

  trait Comparator {
    type T

    def ordering: Ordering[T]

    def compare(a: T, b: T): Int = ordering.compare(a, b)
  }


  implicit object IntComparator extends Comparator {
    override type T = Int

    override def ordering: Ordering[T] = Ordering.Int
  }

  def max(c: Comparator)(items: Seq[c.T]): c.T = {
    items match {
      case a :: b :: tail =>
        val m = if (c.compare(a, b) > 0) a else b
        max(c)(m :: tail)
      case a :: Nil => a
      case Nil => throw new Exception
    }
  }

  println(max(IntComparator)(Seq(1, 5, 3, 9, 4)))
}


object ComparatorExample3 extends App {

  trait Comparator {
    type T

    def ordering: Ordering[T]

    def compare(a: T, b: T): Int = ordering.compare(a, b)
  }

  implicit object IntComparator extends Comparator {
    override type T = Int

    override def ordering: Ordering[T] = Ordering.Int
  }

//  class Process(c: Comparator) {
//    def max(items: Seq[c.T]): c.T = {
//      items match {
//        case a :: b :: tail =>
//          val m = if (c.compare(a, b) > 0) a else b
//          max(m :: tail)
//        case a :: Nil => a
//        case Nil => throw new Exception
//      }
//    }
//  }
//
//  val p = new Process(IntComparator)
//  doesn't compile:
//    println(p.max(Seq(1, 5, 3, 9, 4)))
}


object ComparatorExample4 extends App {

  trait Comparator {
    type T

    def ordering: Ordering[T]

    def compare(a: T, b: T): Int = ordering.compare(a, b)
  }

  implicit object IntComparator extends Comparator {
    override type T = Int

    override def ordering: Ordering[T] = Ordering.Int
  }

  class Process[K](c: Comparator {type T = K}) {
    def max(items: Seq[K]): K = {
      items match {
        case a :: b :: tail =>
          val m = if (c.compare(a, b) > 0) a else b
          max(m :: tail)
        case a :: Nil => a
        case Nil => throw new Exception
      }
    }
  }

  val p = new Process(IntComparator)
  println(p.max(Seq(1, 5, 3, 9, 4)))
}
