import io.circe.generic.semiauto.deriveCodec
import io.circe.{Codec, Decoder}

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