import SealedTraitCodecSample.Event
import eu.timepit.refined.api.Refined
import eu.timepit.refined.collection.NonEmpty
import io.circe.generic.semiauto
import io.circe.{Codec, Decoder, Encoder, KeyDecoder, KeyEncoder}
import io.circe.generic.semiauto._

import scala.collection.mutable


object models {
  case class FooId(value: String)
  case class FooBar(map: Map[FooId, Int])
}

object EventDecoder extends App {

  import io.circe.refined._
  import models._

  implicit val d: Codec[FooId] = deriveCodec[FooId]
  implicit val c : Codec[FooBar] = deriveCodec[FooBar]
  implicit val x1 : Encoder[FooBar] = deriveEncoder[FooBar]
  implicit val ke: KeyEncoder[FooId] = (key: FooId) => key.value
  implicit val kd: KeyDecoder[FooId] = (key: String) => Some(FooId(key))

  import io.circe.syntax._


  val a =mutable.HashMap( FooId("A") -> 6)
  println(a.asJson)

}
