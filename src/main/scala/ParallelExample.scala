import cats.Applicative

import scala.concurrent.Future

object ParallelExample extends App {
  import cats.instances.future._
  import cats.syntax.all._

  import scala.concurrent.ExecutionContext.Implicits.global

  val a = Applicative[Future].pure(1)
  val b = Applicative[Future].pure(2)

  println((a, b).mapN(_ + _))
  import cats.~>

  val first: List ~> Option = λ[List ~> Option](_.headOption)
  // import cats.implicits._

  import cats.data._
  // import cats.data._

  case class Name(value: String)
  // defined class Name

  case class Age(value: Int)
  // defined class Age

  case class Person(name: Name, age: Age)
  // defined class Person

  def parse(s: String): Either[NonEmptyList[String], Int] = {
    if (s.matches("-?[0-9]+")) Right(s.toInt)
    else Left(NonEmptyList.one(s"$s is not a valid integer."))
  }
  // parse: (s: String)Either[cats.data.NonEmptyList[String],Int]

  def validateAge(a: Int): Either[NonEmptyList[String], Age] = {
    if (a > 18) Right(Age(a))
    else Left(NonEmptyList.one(s"$a is not old enough"))
  }
  // validateAge: (a: Int)Either[cats.data.NonEmptyList[String],Age]

  def validateName(n: String): Either[NonEmptyList[String], Name] = {
    if (n.length >= 8) Right(Name(n))
    else Left(NonEmptyList.one(s"$n Does not have enough characters"))
  }
  
//  import cats.implicits._
//  def parsePerson(ageString: String, nameString: String) =
//    for {
//      age <- parse(ageString)
//      person <- (validateName(nameString), validateAge(age)).mapN(Person)
//    } yield person

}
