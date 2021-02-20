package dependenttype

object AbsVsParamTypes extends App {

  abstract class Animal {
    type SuitableFood <: Food

    def eatMeal(meal: SuitableFood): Unit
  }

  class Lion extends Animal {
    type SuitableFood <: Meat

    override def eatMeal(meal: SuitableFood): Unit =
      println("Lion's eating meat!")
  }

  class Cow extends Animal {
    type SuitableFood <: Grass

    override def eatMeal(meal: SuitableFood): Unit =
      println("Cow's eating grass!")
  }

  val lion = new Lion() {
    type SuitableFood = Meat
  }

  val cow = new Cow() {
    type SuitableFood = Grass
  }

  cow.eatMeal(new Grass)
  lion.eatMeal(new Meat)

  abstract class Food
  class Grass extends Food
  class Meat extends Food

}

object Test extends App {
  trait FoodStuff
  trait Meat extends FoodStuff {
    type IsMeat = Any
  }
  trait Grass extends FoodStuff {
    type IsGrass = Any
  }
  trait Animal {
    type Food <: FoodStuff
    def eats(food: Food): Unit
    def gets: Food
  }
  trait Cow extends Animal {
    type IsMeat = Any
    type Food <: Grass
    def eats(food: Grass): Unit
    def gets: Food
  }
  trait Lion extends Animal {
    type Food = Meat
    def eats(food: Meat): Unit
    def gets: Meat
  }
  def newMeat: Meat = new Meat {}
  def newGrass: Grass = new Grass {}
  def newCow: Cow =
    new Cow {
      type Food = Grass
      def eats(food: Grass) = ()
      def gets = newGrass
    }
  def newLion: Lion =
    new Lion {
      def eats(food: Meat) = ()
      def gets = newMeat
    }
  val milka = newCow
  val leo = newLion
//  leo.eats(milka) // structural select not supported
}

object AnimalFood extends App {
  trait Animal { a =>
    type Food
    def eats(food: a.Food): Unit = {}
    def gets: a.Food
  }

  trait Grass
  trait Meat

  trait Cow extends Animal with Meat {
    type Food = Grass
    def gets = new Grass {}
  }

  trait Lion extends Animal {
    type Food = Meat
    def gets = new Meat {}
  }

  val leo = new Lion {}
  val milka = new Cow {}

  leo.eats(milka)
  leo.eats(leo.gets)


  val lambda: Animal = milka
  lambda.eats(lambda.gets)
}
