package dependenttype

object SemigroupTypeClassExample {
  import Alias._

  ?[Semigroup[Int]](IntSemigroup)
}


trait Semigroup_ {
 type F
 def append(a: F, b: => F): F
}

object IntSemigroup extends Semigroup_ {
  override type F = Int

  override def append(a: Int, b: => Int): Int = a + b
}

object Alias {
  type Semigroup[G] = Semigroup_{type F = G}

  def ?[A <: AnyRef](implicit a: A): a.type = a
}