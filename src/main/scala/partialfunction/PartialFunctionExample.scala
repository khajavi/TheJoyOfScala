package partialfunction

object PartialFunctionExample extends App {
  val pf                                       = PartialFunction.fromFunction((x: Int) => x * x)
  val _pf          : PartialFunction[Int, Int] = {
    case x: Int if x > 0 => x * x
  }
  val totalFunction: Int => Option[Int]        = _pf.lift
  val idDefined                                = _pf.isDefinedAt(0)
  val result                                   = _pf.lift(10)
  val _result                                  = _pf.unapply(10)
  _pf.apply(10)
  val empty_pf: PartialFunction[Int, String] = PartialFunction.empty[Int, String]
}

object MyPartialFunction extends App {

  trait MyPFunction[-A, +B] extends (A => B) {
    def isDefinedAt(i: A): Boolean
  }

  object MyPFunction {
    def empty[A, B]: MyPFunction[A, B] = new MyPFunction[Any, Nothing] {
      override def isDefinedAt(i: Any): Boolean = false

      override def apply(v1: Any): Nothing = throw new MatchError(v1)
    }
    
    def fromFunction[A, B](f:  A => B): PartialFunction[A, B] = {case x => f(x)}
  }
  

}
