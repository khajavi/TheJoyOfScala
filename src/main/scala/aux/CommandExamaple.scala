package aux

import cats.syntax.either._
import io.circe.Json

object CommandExamaple extends App {

}

trait Command {
  type Out
}

object Command {
  type AUX[R] = Command {type Out = R}
}

trait CommandDecoder[C <: Command.AUX[R], R] {
  def decode(json: Json): Either[String, R]
}

object Members extends Command {
  type Out = List[String]
}

object memberInstance extends CommandDecoder[Members.type, List[String]] {
  override def decode(json: Json): Either[String, List[String]] =
    json.as[List[String]].leftMap(_.getMessage())
}
