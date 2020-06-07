package circe

object ParseArrayExample extends App {
  import io.circe._

  val result1 = parser.parse("[1,2,3]").map(_.as[List[Int]]).getOrElse(Right(List.empty[Int])).getOrElse(List.empty[Int])
  println(result1)

  val result2 = parser.parse("[[0,1], [3]]").map(_.asArray.map(_.map(_.asArray.map(_.map(_.as[Int])))))
  println(result2)

  val result3 = parser.parse("[[0,1], [3]]").map(_.asArray)
}
