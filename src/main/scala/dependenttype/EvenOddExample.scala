package dependenttype

import dependenttype.EvenOddExample.`42`

/**
  * @author Milad Khajavi <khajavi@gmail.com>.
  */
object EvenOddExample extends App {
  val `42` = Integer(42)
  implicit val proof42IsEven: `42`.IsEven.type = `42`.IsEven
  Integer.f(`42`)
}

final case class Integer(v: Int) {
  object IsEven {
    require(v % 2 == 0)
  }

  object IsOdd {
    require(v % 2 != 0)
  }

}

object Integer {
  def f(n: Integer)(implicit even: n.IsEven.type) = {
   println("hello")
  }
}

