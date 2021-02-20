package readermonad

import cats.data

object ReaderMonadExample extends App {

  case class Reader[E, A](run: E => A) {
    def flatMap[B](f: A => Reader[E, B]): Reader[E, B] =
      Reader[E, B](x => f(run(x)).run(x))

    def map[B](f: A => B): Reader[E, B] =
      Reader[E, B](x => f(run(x)))
  }

  object Reader {
    def ask[R]: Reader[R, R] = Reader(identity)
  }
  
  val reader = for {
    x <- Reader[Int, String](x => x.toString.toUpperCase)
  } yield (x)

  println(reader.run(5))
}

object CatsReaderMonad extends App {
  import cats.implicits._ 
  
  val res = for {
    x <- cats.data.ReaderT[Option, Int, Int](x => Some(x * x)) 
  } yield (x)

  println(res.run(8))

}


