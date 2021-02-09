package generics


// Borrow example from https://stackoverflow.com/a/48858344/225052
object ContravariantExample extends App {

  sealed trait EnergySource

  case class Meat() extends EnergySource

  case class Vegetables() extends EnergySource

  case class Bamboo() extends EnergySource

  trait Consumer[-T] {
    def consume(t: T): Unit
  }
  
  // Whenever we need EnergySource we can substitute it with Meat or Vegetables or Bambo 
  // Meat <: EnergySource
  // vegetable <: EnergySource
  // Bamboo <: EnergySource
  
  //  Whenever we need Consumer[Meat] we can substitute it with Consumer[EnergySource]
  //  Consumer[EnergySource] <: Consumer[Meat] 
  //  Whenever we need Consumer[Vegetables] we can substitute it with Consumer[EnergySource]
  //  Consumer[EnergySource] <: Consumer[Vegetables]
  //  Whenever we need Consumer[Bamboo] we can substitute it with Consumer[EnergySource]
  //  Consumer[EnergySource] <: Consumer[Bamboo]
  

  val fire = new Consumer[EnergySource] {
    override def consume(t: EnergySource): Unit = t match {
      case Meat() => println("burn meat")
      case Vegetables() => println("burn vegetable")
      case Bamboo() => println("burn bambo")
    }
  }

  val herbivore = new Consumer[Vegetables] {
    override def consume(t: Vegetables): Unit = t match {
      case Vegetables() => println("eat vegetable")
    }
  }
 
  // doesn't compile 
  // herbivore.consume(Meat())

}
