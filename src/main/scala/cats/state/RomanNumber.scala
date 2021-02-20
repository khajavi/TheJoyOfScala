package cats.state
import cats.implicits._
import cats.data.{OptionT, State}
import cats.{Order, effect}
import cats.effect.{ExitCode, IOApp}

object RomanNumber extends IOApp {
  override def run(args: List[String]): effect.IO[ExitCode] = {
    import Parse._
    val r = combineStates().run(RInput(List(RSymM, RSymD, RSymM, RSymC, RSymL, RSymC, RSymM)))
    effect.IO(println(r.value)).as(ExitCode.Success)
  }
}

object Parse {
  abstract class ARoman(val symbol: Char, val value: Int)
  case object RSymI extends ARoman('I', 1)
  case object RSymV extends ARoman('V', 5)
  case object RSymX extends ARoman('X', 10)
  case object RSymL extends ARoman('L', 50)
  case object RSymC extends ARoman('C', 100)
  case object RSymD extends ARoman('D', 500)
  case object RSymM extends ARoman('M', 1000)

  object ARoman {
    implicit val order: Order[ARoman] = Order.from { (a, b) =>
      a.value.compareTo(b.value)
    }
  }
  sealed trait Expr[A]
  case class Literal[A](value: A) extends Expr[A]
  case class Plus[A](a: A, b: A) extends Expr[A]
  case class Minus[A](a: A, b: A) extends Expr[A]

  case class RInput(chars: List[ARoman])

  type RState[A] = State[RInput, A] // RInput => (RInput, A)

  def pop(): RState[ARoman] =
    State {
      case RInput(x :: xs) =>
        (RInput(xs), x)
      case RInput(Nil) =>
      throw new Exception
    }

  def peek(): RState[Option[ARoman]] =
    State { in =>
      (in, in.chars.headOption)
    }

  def pairwise(): RState[Expr[ARoman]] =
    for {
      a <- pop()
      b <- (OptionT(peek()) *> OptionT(pop().map(Option(_)))).value
      expr = b.fold[Expr[ARoman]](Literal(a)) { b =>
        if (a >= b) Plus(a, b) else Minus(a, b)
      }
    } yield expr

  def combineStates(): RState[List[Expr[ARoman]]] =
    for {
      expr <- pairwise()
      s <- State.get
      exprList <-
        if (s.chars.size > 1) combineStates()
        else pop().map(x => List(Literal(x)))
    } yield expr :: exprList

}

object ABF {
 val a =  State.get[String]
}