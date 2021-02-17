package writermonad

object WriterMonadExample extends App {

  import cats.data.Writer
  import cats.implicits._

  val w1 = Writer[Vector[String], Int](Vector("genesis log"), 123)
  val w2 = w1.tell(Vector("Hello"))
  val w3 = w2.tell(Vector("Goodby"))
  println(w3.run)

  type Logged[A] = Writer[Vector[String], A]

  val result = for {
    a <- 10.pure[Logged]
    _ <- Vector("a", "b").tell
    b <- 32.writer(Vector("foo", "bar"))
  } yield (a + b)

  println(result.run)

 val res2 = result.bimap(
    _.map(_.toUpperCase),
    _ * 100
  )
  
  println(res2.run)

}
