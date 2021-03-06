import eu.timepit.refined.api.Refined
import eu.timepit.refined.collection.NonEmpty
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

case class SolrDoc(response: SolrResponse)

object SolrDoc {
  implicit val comCodec: Codec[SolrDoc] = deriveCodec[SolrDoc]
}

case class SolrResponse(docs: Seq[Article])

object SolrResponse {
  implicit val solrResponseCodec: Codec[SolrResponse] = deriveCodec[SolrResponse]
}

case class Article()

object Article {
  implicit val articleCodec: Codec[Article] = deriveCodec[Article]
}

object CirceDecoderSample extends App {

  import io.circe.syntax._

  SolrDoc(SolrResponse(Seq(Article()))).asJson
}

object SealedTraitCodecSample extends App {

  import io.circe.refined._
  import eu.timepit.refined.auto._
  sealed trait Event

  type Message = Refined[String, NonEmpty]

  case class A(msg: Message) extends Event

  case class B(msg: String) extends Event

  implicit val codec: Codec[Event] = deriveCodec[Event]

  import io.circe.syntax._

  val a: Event = A("hello")
  val b        = implicitly[Codec[Event]]
  println(a.asJson)
}