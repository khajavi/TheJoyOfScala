import scala.language.higherKinds

object KindProjectorSample extends App {
  type IntOrA[A] = Either[Int, A]
  val foo1: IntOrA[String] = Right("Milad")
  val bar1: IntOrA[String] = Left(4)
  println(foo1, bar1)


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

//  def foo[A[_, _], B](functor: Functor[A[B, ?]]) = ???

  // 1. Type projection
  def foo[A[_, _], B](functor: Functor[({type AB[C] = A[B, C]})#AB]) = ???

  //2. Kind projector
  def foo2[A[_, _], B](functor: Functor[A[B, *]]) = ???

  //3. declare surrounding class
  def foo3[A[_, _], B] = new Foo[A, B]

  class Foo[A[_, _], B] {
    type AB[C] = A[B, C]
    def apply(functor: Functor[AB]): Foo[A, B] = new Foo[A, B]
  }

  //4. curried type constructors
//  type AB[A, B][C] = A[B, C]   not valid syntax yet

//  def foo[A[_, _], B](functor: Functor[AB[A, B]])

  type a = Functor[Lambda[a => List[Seq[a]]]]

}


object TypeConstructor extends App {
  type A[B] = Option[B][String]

  val a: A[String] = Some("foo")
  println(a)
}
