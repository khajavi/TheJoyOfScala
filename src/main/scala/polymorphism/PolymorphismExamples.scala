package polymorphism

import scala.annotation.tailrec

object ParametricPolymorphismExample extends App {
  def length[T](list: List[T]): Int = {
    @tailrec
    def go(xs: List[T], count: Int): Int =
      xs match {
        case ::(_, next) => go(next, count + 1)
        case Nil         => count
      }

    go(list, 0)
  }

  def length_[T](list: List[T]): Int =
    list.foldLeft(0)((x, _) => x + 1)

  println(length(List(1, 2, 3)))
  println(length(List("1", "b")))
  println(length_(List("foo", "Bar", "Baz")))
}

object SubTypePolymorphismExample extends App {
  trait Shape {
    def area: Double
  }

  case class Square(side: Double) extends Shape {
    override def area: Double = side * side
  }

  case class Circle(radius: Double) extends Shape {
    override def area: Double = Math.PI * radius * radius
  }

  def printArea[T <: Shape](shape: T): Unit = println(shape.area.toString)

  printArea(Square(10.0))
  printArea(Circle(3.0))
}

object AdHocPolymorphismExample extends App {

  case class Square(side: Double) {
    def area: Double = side * side
  }

  case class Circle(radius: Double) {
    def area: Double = Math.PI * radius * radius
  }

  trait Show[T] {
    def show(t: T): String
  }

  def printShape[T: Show](t: T) = println(implicitly[Show[T]].show(t))

  implicit object squareShowInstance extends Show[Square] {
    override def show(t: Square): String =
      s"square with area: ${t.area.toString}"
  }

  implicit object circleShowInstance extends Show[Circle] {
    override def show(t: Circle): String =
      s"circle with area: ${t.area.toString}"
  }

  printShape(Circle(24.4))
  printShape(Square(2.35))
}
