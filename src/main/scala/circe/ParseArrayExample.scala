package circe

object ParseArrayExample extends App {

  import io.circe._

  val result1 = parser.parse("[1,2,3]").map(_.as[List[Int]]).getOrElse(Right(List.empty[Int])).getOrElse(List.empty[Int])
  println(result1)

  val result2 = parser.parse("[[0,1], [3]]").map(_.asArray.map(_.map(_.asArray.map(_.map(_.as[Int])))))
  println(result2)

  val result3 = parser.parse("[[0,1], [3]]").map(_.asArray)
}


object A extends App {
  import io.circe.generic.semiauto.deriveDecoder

  val rawData: String =
    """
    [
        {
        "statementID": 203738,
        "com_ID": 1661,
        "bourseSymbol": "فارس"
        }
    ]
    """

  case class SampleClass(statementID: Int, com_ID:Int, bourseSymbol: String)

  implicit val SampleClassDecoder = deriveDecoder[SampleClass]

import io.circe.parser
  val b = parser.parse(rawData).map(_.as[List[SampleClass]])
  b match {
    case Right(x) => {
      println("================================================")
      println("You passed me the Right: " + x)

    }
    case Left(x) => {
      println("================================================")
      println("You passed me the Left Error: " + x.getMessage())
    }
  }
}
