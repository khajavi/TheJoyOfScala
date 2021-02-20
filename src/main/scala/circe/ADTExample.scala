package circe

sealed trait Event

case class Foo(i: Int) extends Event
case class Bar(s: String) extends Event
case class Baz(c: Char) extends Event
case class Qux(values: List[String]) extends Event

import cats.syntax.functor._
import io.circe.{Decoder, Encoder}, io.circe.generic.auto._
import io.circe.syntax._

object ADTExample extends App {
  val f = Foo(2)

  val json = implicitly[Encoder[Event]](GenericDerivation.encodeEvent).apply(f)
  println(json)
}

object GenericDerivation {
  implicit val encodeEvent: Encoder[Event] = Encoder.instance {
    case foo @ Foo(_) => foo.asJson
    case bar @ Bar(_) => bar.asJson
    case baz @ Baz(_) => baz.asJson
    case qux @ Qux(_) => qux.asJson
  }

  implicit val decodeEvent: Decoder[Event] =
    List[Decoder[Event]](
      Decoder[Foo].widen,
      Decoder[Bar].widen,
      Decoder[Baz].widen,
      Decoder[Qux].widen
    ).reduceLeft(_ or _)
}
