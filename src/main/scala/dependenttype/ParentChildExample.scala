package dependenttype

object DriveAndWashingExample extends App {
  val john =  Driver("John")
  val scarlet = Driver("Scarlet")

  john.drive(john.car)
//  john.drive(scarlet.car)
}

case class Driver(name: String) {
  class Car

  def car = new this.Car

  def drive(c: this.Car): Unit =
    println(s"driving $c")

  def wash(c: Driver#Car): Unit =
    println(s"washing $c")
}
