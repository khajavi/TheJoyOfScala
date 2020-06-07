package classes

import eu.timepit.refined
import eu.timepit.refined.W
import eu.timepit.refined.api.Refined
import eu.timepit.refined.auto._
import eu.timepit.refined.boolean.{AllOf, Not, OneOf, Or}
import eu.timepit.refined.char.LetterOrDigit
import eu.timepit.refined.collection.{MaxSize, Tail}
import eu.timepit.refined.generic.Equal
import eu.timepit.refined.string.{MatchesRegex, StartsWith}
import shapeless.HNil
import shapeless.::
import shapeless.HNil

object A extends App {
  type Five = W.`5`.T
  type FivePredicate = Equal[Five]
  type One = W.`1`.T
  type OnePredicate = Equal[One]

  val foo: Refined[Int, FivePredicate] = 5

  val bar: Refined[Int, Or[OnePredicate , FivePredicate]] = 1

  val tux: Refined[Int, OneOf[OnePredicate :: FivePredicate :: HNil]] = refined.refineMV(1)
  println(tux)

  type TwitterHandle = String Refined AllOf[
    StartsWith[W.`"@"`.T] :: MaxSize[W.`16`.T] ::
      Not[MatchesRegex[W.`"(?i:.*twitter.*)"`.T]] ::
      Not[MatchesRegex[W.`"(?i:.*admin.*)"`.T]] ::
      Tail[Or[LetterOrDigit, Equal[W.`'_'`.T]]] ::
      HNil
  ]

  val handler: TwitterHandle = "@khajavi"
  println(handler)

}

