package currying

object CurryingExample extends App {
  val sum    : (Int, Int) => Int = (x, y) => x + y
  val numbers: List[Int]         = List(1, 2, 3)

  println(numbers.map(sum(1, _)))


  def sumF(i: Int, b: Int)(f: Int => Int): Int = ???

  val _sum      : (Int, Int, Int => Int) => Int     = sumF(_, _)(_)
  val __sum     : (Int, Int => Int) => Int          = sumF(1, _)(_)
  val ___sum    : (Int, Int) => (Int => Int) => Int = sumF
  val ____sum   : (Int, Int) => (Int => Int) => Int = sumF(_, _) _
  val _____sum  : Int => (Int, Int => Int) => Int   = (x: Int) => sumF(x, _)(_)
  val ______sum : Int => Int => (Int => Int) => Int = (x: Int) => (y: Int) => sumF(x, y) _
  val _______sum: Int => Int => Int => Int          = (x: Int) => (y: Int) => (z: Int) => sumF(x, y)(_ => z)


  case class Event(a: Int, b: String)

  val event  : Int => String => Event   = Event.curried
  val eventTupled: ((Int, String)) => Event = Event.tupled
  
}
