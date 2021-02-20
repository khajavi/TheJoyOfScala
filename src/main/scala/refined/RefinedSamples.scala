package refined

trait FooBar
//import eu.timepit.refined.api.Refined
//import eu.timepit.refined.boolean.And
//import eu.timepit.refined.char.{Letter, UpperCase}
//import eu.timepit.refined.collection.{Forall, Index, NonEmpty}
//import eu.timepit.refined.types.string.NonEmptyString

//object RefinedSamples extends App {
//  type Name = Refined[String, Forall[Letter] And Index[0, UpperCase] And NonEmpty]
//
//  val name: Name = "Milad"
//  println(name)
//}
//
//
//object SemigroupRefined extends App {
//
//  import cats.implicits._
//
//  implicit object nonEmptyStringSemigroup extends cats.Semigroup[NonEmptyString] {
//    override def combine(x: NonEmptyString, y: NonEmptyString): NonEmptyString =
//      Refined.unsafeApply(x.value + y.value)
//  }
//
//  val gg = 1 |+| 2
//  val s1: NonEmptyString = "Hello"
//  val s2: NonEmptyString = " World!"
//  val s3 = s1 |+| s2
//  println(s3)
//}
//
