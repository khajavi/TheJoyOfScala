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


trait Currency {
  val value: Int
}

object Currency {
  def apply(value: Int): Currency = new Currency {
    override val value: Int = value
  }

  def unapply(arg: Currency): Some[Int] = Some(arg.value)

  implicit val currencyCodec = deriveCodec[Currency]
}

object A extends App {
  val c = Currency(5)

  import io.circe.syntax._

  println(c.asJson)
}