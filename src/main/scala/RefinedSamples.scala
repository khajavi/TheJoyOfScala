import eu.timepit.refined.api.Refined
import eu.timepit.refined.auto._
import eu.timepit.refined.boolean.And
import eu.timepit.refined.char.{Letter, UpperCase}
import eu.timepit.refined.collection.{Forall, Index, NonEmpty}

object RefinedSamples extends App {
  type Name = Refined[String, Forall[Letter] And Index[0, UpperCase] And NonEmpty]

  val name: Name = "Milad"
  println(name)
}
