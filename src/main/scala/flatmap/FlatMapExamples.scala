package flatmap

import cats.FlatMap

/**
  * @author Milad Khajavi <khajavi@gmail.com>.
  */
object FlatMapExamples {

  object ListExample extends App {
    def pure[A](x: A): List[A] = List(x)

    def flatMap[A, B](list: List[A], f: A => List[B]): List[B] = {
      list.map(f).flatten
    }

  }

}
