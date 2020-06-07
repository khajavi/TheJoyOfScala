object PolymorphismExamples extends App {
  def plus[A](a1: A, a2: A): A = ???

  trait CanPlus[A] {
    def plus(a1: A, a2: A): A
  }

  def plus[A: CanPlus](a1: A, a2: A): A =
    implicitly[CanPlus[A]].plus(a1, a2)

  println("hello")
}
