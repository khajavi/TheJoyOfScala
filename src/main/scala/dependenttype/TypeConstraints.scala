package dependenttype

sealed abstract class =:=[From, To] extends <:<[From, To] with Serializable {
  override def substituteBoth[F[_, _]](ftf: F[To, From]): F[From, To]
  override def substituteCo[F[_]](ff: F[From]): F[To] = {
    type G[_, T] = F[T]
    val fff: G[To, From] = ff
    substituteBoth(fff)
  }
}
sealed abstract class <:<[-From, +To] extends (From => To) with Serializable {
  def substituteBoth[F[-_, +_]](ftf: F[To, From]): F[From, To]
  def substituteCo[F[+_]](ff: F[From]): F[To] = {
    type G[-_, +T] = F[T]
    val fff: G[To, From] = ff
    substituteBoth(fff)
  }
  override def apply(f: From): To = {
    type Id[+X] = X
    val ff: Id[From] = f
    substituteCo(ff)
  }
}

object <:< {
  private val singleton: =:=[Any, Any] = new =:=[Any, Any] {
    override def substituteBoth[F[_, _]](ftf: F[Any, Any]): F[Any, Any] = ftf
  }
  implicit def refl[A]: =:=[A, A] = singleton.asInstanceOf[=:=[A, A]]
}

object ABC extends App {
  abstract class SubtypeOf[-From, +To] {
    def substituteBoth[F[-_, +_]](ftf: F[To, From]): F[From, To]
    def substituteCo[F[+_]](ff: F[From]): F[To] = {
      type G[-_, +T] = F[T]
      substituteBoth[G](ff)
    }
    def apply(f: From): To = {
      type Id[+X] = X
      substituteCo[Id](f)
    }
  }

  val eq: SubtypeOf[Any, Any] = new SubtypeOf[Any, Any] {
    override def substituteBoth[F[-_, +_]](ftf: F[Any, Any]): F[Any, Any] = ftf
  }

//  implicit val f = new SubtypeOf[Bar, Foo] {
//    override def substituteBoth[F[-_, +_]](ftf: F[Foo, Bar]): F[Bar, Foo] = ftf
//  }

  implicit def ref1[A]: SubtypeOf[A, A] = eq.asInstanceOf[SubtypeOf[A, A]]

  type A = Int
  type B = Int

  class Foo
  class Bar extends Foo

  implicitly[A SubtypeOf B]
  implicitly[Bar SubtypeOf Foo]
}
