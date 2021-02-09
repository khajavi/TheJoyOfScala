package generics

// Borrow example from https://www.baeldung.com/scala/generics-basics
object UpperBoundExample extends App {
  def findMax[T <: Ordered[T]](xs: List[T]): Option[T] =
    xs.reduceOption((x1, x2) => if (x1 > x2) x1 else x2)
}

object LowerBoundExample extends App {

  case class Box[+T](private val value: T) {
    def set[U >: T](v: U): Box[U] = Box(v)
  }

  sealed abstract class Animal extends Product

  case class Cat() extends Animal

  case class Dog() extends Animal

  // Cat <: Animal
  // Dog <: Animal
  // Box[Cat] <: Box[Animal]
  // Box[Dog] <: Box[Animal]

  val catBox      : Box[Cat]         = Box(Cat())
  val dogBox      : Box[Dog]         = Box(Dog())
  val animalCatBox: Box[Animal]      = Box(Cat())
  val animalDogBox: Box[Animal]      = Box(Dog())
  val animalBoxes : Seq[Box[Animal]] = Seq(catBox, dogBox)
  val cat                            = Cat()
  dogBox.set(cat)
}


