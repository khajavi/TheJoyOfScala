
object KindProjectorSample extends App {
  type IntOrA[A] = Either[Int, A]
  val foo: IntOrA[String] = Right("Milad")
  val bar: IntOrA[String] = Left(4)
  println(foo, bar)


  trait Functor[F[_]]
  type F1 =  Functor[Option]
  type F2 = Functor[List]
//  type F3 = Functor[Map] //Error: Error:(12, 21) Map takes two type parameters, expected: one
  type IntKeyMap[A] = Map[Int, A]
  type F3 = Functor[IntKeyMap]

//  type F4 = Functor[Map[Int, _]]
// Error: Map[Int, _] takes no type parameters, expected: one

  type F5 = Functor[({type T[A] = Map[Int , A]})#T]

  type F6 = Functor[Map[Int, *]]
}

