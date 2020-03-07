import cats.{FlatMap, Functor}

/**
 * @author Milad Khajavi <khajavi@gmail.com>
 */
object CatsExamples extends App {

  import cats.implicits._

  //  val parse: Kleisli[Option,String,Int] =
  //    Kleisli((s: String) => if (s.matches("-?[0-9]+")) Some(s.toInt) else None)
  //
  //  val reciprocal: Kleisli[Option,Int,Double] =
  //    Kleisli((i: Int) => if (i != 0) Some(1.0 / i) else None)
  //
  //  val parseAndReciprocal: Kleisli[Option,String,Double] =
  //    reciprocal.compose(parse)


  final case class MyKleisli[F[_], A, B](run: A => F[B]) {
    def map[C](f: B => C)(implicit F: Functor[F]): MyKleisli[F, A, C] = {
      MyKleisli(a => F.map(run(a))(f))
    }
  }

  val parse: MyKleisli[Option, String, Int] =
    MyKleisli((s: String) => if (s.matches("-?[0-9]+")) Some(s.toInt) else None)

  val reciprocal: MyKleisli[Option, Int, Double] =
    MyKleisli((i: Int) => if (i != 0) Some(1.0 / i) else None)

//  val parseAndReciprocal: MyKleisli[Option, String, Double] =
//    reciprocal.compose(parse)

//  println(parseAndReciprocal.run("12"))


}


object e extends App {



}

trait DB[F[_]] {
}

//trait MyDB extends DB[IntOrA] {
//
//}
//
//trait MyDB2 extends DB[({type L[A] = Either[Int, A]})#L] {
//
//}