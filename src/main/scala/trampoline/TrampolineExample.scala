package trampoline

import cats.data.State

import scala.annotation.tailrec

object TrampolineExampleV1 extends App {
  def even[A](list: List[A]): Boolean = list match {
    case ::(_, next) => odd(next)
    case Nil => true
  }

  def odd[A](list: List[A]): Boolean = list match {
    case ::(_, next) => even(next)
    case Nil => false
  }
}

object TrampolineExampleV2 extends App {

  object v1 {

    sealed trait Trampoline[A]

    case class Done[A](value: A) extends Trampoline[A]

    case class More[A](call: () => Trampoline[A]) extends Trampoline[A]

    def even[A](list: List[A]): Trampoline[Boolean] = list match {
      case ::(_, next) => More(() => odd(next))
      case Nil => Done(true)
    }

    def odd[A](list: List[A]): Trampoline[Boolean] = list match {
      case ::(_, next) => even(next)
      case Nil => Done(false)
    }

    @tailrec
    def run[A](trampoline: Trampoline[A]): A = trampoline match {
      case Done(value) => value
      case More(call) => run(call())
    }
  }

  object v2 {

    sealed trait Trampoline[A]

    case class Done[A](value: A) extends Trampoline[A]

    case class More[A](call: () => Trampoline[A]) extends Trampoline[A]

    def resume[A](t: Trampoline[A]): Either[() => Trampoline[A], A] = t match {
      case Done(value) => Right(value)
      case More(call) => Left(call)
    }

    @tailrec
    def run[A](t: Trampoline[A]): A = t match {
      case Done(value) => value
      case More(call) => run(call())
    }

  }

  object v3 {

    sealed trait Trampoline[A] {
      def resume: Either[() => Trampoline[A], A] = this match {
        case Done(value) => Right(value)
        case More(call) => Left(call)
      }

      @tailrec
      final def runT: A = this match {
        case Done(value) => value
        case More(call) => call().runT
      }
    }

    case class Done[A](value: A) extends Trampoline[A]

    case class More[A](call: () => Trampoline[A]) extends Trampoline[A]

  }

  object v4 {

    case class State[S, A](run: S => (S, A)) {
      def map[B](f: A => B): State[S, B] = State { s =>
        val (newState, a) = run(s)
        (newState, f(a))
      }

      def flatMap[B](f: A => State[S, B]): State[S, B] = State { s =>
        val (newState, a) = run(s)
        f(a).run(newState)
      }
    }

  }

  object v5 {
    sealed trait Computation[A]
    case class Suspend[A](next: () => Computation[A]) extends Computation[A]
    case class Done[A](result: A) extends Computation[A]
    
    @inline def done[A](r: A) = Done(r)
    @inline def suspend[A](r: => Computation[A]) = Suspend(() => r)
    
    @tailrec
    final def run[A](computation: Computation[A]): A = computation match {
      case Suspend(next) => run(next())
      case Done(result) => result
    }
  }


}

