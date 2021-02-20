
val l: List[Int] = List(1,2,3)
println(l)

import shapeless.HList._
import shapeless._
val hlist: ::[Int, ::[Double, ::[String, ::[Boolean, HNil]]]] = 1 :: 1.0 :: "One" :: false :: HNil
println(hlist)

case class Book(name: String, price: Double)
val genericBook = Generic[Book]
val b = Book("Folan", 1.4)
val r = genericBook.to(b)

case class Red()
case class Blue()
case class Green()

type Light = Red :+: Blue :+: Green :+: CNil

val red: Light = Inl(Red())
val green: Light = Inr(Inr(Inl(Green())))

//sealed trait Error
//case class BadName(name: String) extends Error
//case class BadAge(age: Int) extends Error
//
//BadName("") match {
//  case BadName(name)  => s"$name is bad"
//}

case class BadName(name: String)
case class BadAge(age: Int)
type Error = BadName :+: BadAge :+: CNil

object errorHandler1 extends Poly1 {
  implicit def name = at[BadName] { e => s"bad first name: ${e.name}" }
  implicit def age = at[BadAge] { e => s"bad age: ${e.age}" }
}
import shapeless.Coproduct
val nameError1 = Coproduct[Error](BadName("John"))
val errorMessage1 = nameError1.fold(errorHandler1)
println(errorMessage1)

object errorHandler2 extends Poly1 {
  implicit def name = at[BadName] { e => "BAD NAME" }
  implicit def age = at[BadAge] { e => -1 }
}

val nameError2 = Coproduct[Error](BadName("John"))
val errorMessage2 = nameError2.map(errorHandler2)

val maybeStringError: Option[String] = errorMessage2.select[String]
val maybeIntError: Option[Int] = errorMessage2.select[Int]

import shapeless.HNil
import shapeless.syntax.singleton._

val garField = ("cat" ->> "Garfield") :: ("orange" ->> true) :: HNil

sealed trait Shape
final case class Rectangle(width: Double, height: Double) extends Shape
final case class Circle(radius: Double) extends Shape

val gen = Generic[Shape]
