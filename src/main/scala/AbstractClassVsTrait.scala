
abstract class Logger[T](prefix: T) {
  protected val p: T = prefix

  def log(str: T): Unit
}

trait Printer[T] {
  def print(str: T): Unit

  def printLine(): Unit = println("---------------")
}


object AbstractClassVsTrait extends App {

  val logger = new Logger[String]("hello") {

    override def log(str: String): Unit = println(p + str)
  }

  logger.log("Every Thing is Ok")
}


trait AA {
  def a = 1
}

trait X extends AA {
  override def a: Int = {
    println('X')
    super.a
  }
}

trait Y extends AA {
  override def a: Int = {
    println("Y")
    super.a
  }
}

object StackableTraits extends App {
  val xy = new AnyRef with X with Y
  xy.a
}


class N {
  type A
}


sealed class WeekDay(val value: Int)

object WeekDay {

  case object Monday extends WeekDay(0)

  case object Tuesday extends WeekDay(1)

  case object Wednesday extends WeekDay(2)

  case object Thursday extends WeekDay(3)

  case object Friday extends WeekDay(4)

  case object Saturday extends WeekDay(5)

  case object Sunday extends WeekDay(6)

}

object GG extends App {
  println(WeekDay.Friday)
}


//class Food
//
//abstract class Animal {
//  type SuitableFood <: Food
//
//  def eat(food: SuitableFood)
//}
//
//class Grass extends Food
//
//class Cow extends Animal {
//  override type SuitableFood = Grass
//
//  override def eat(food: Grass) {} // This won't compile,
//} // but if it did,...
//class Fish extends Food
//
//object H  {
//  val bessy: Animal = new Cow
//  bessy.eat(new Fish)
//}
//



class Food

trait Animal {
  type SuitableFood <: Food

  def eat(foot: SuitableFood): Unit

  def makeFood(): SuitableFood
}

class Grass extends Food

class Cow extends Animal {
  type SuitableFood = Grass

  override def eat(food: Grass) : Unit = {
    println("cow eat grass")
  }

  override def makeFood() = new Grass()
}

class Fish extends Food

object HA extends App {
  val bessy: Animal = new Cow
  bessy.eat(bessy.makeFood())
//  bessy.eat(new Grass())
}


 trait IntQueue {
  def get(): Int
  def put(x: Int): Unit
}

object IntQueue {
  def size(): Int = 3
}

