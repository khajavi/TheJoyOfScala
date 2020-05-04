package variance

/**
 * https://www.freecodecamp.org/news/understand-scala-variances-building-restaurants/
 */
object VarianceExamples extends App {
  // Food <- Meat
  val beef = new Meat("beef")

  // Food <- Meat <- WhiteMeat
  val chicken = new WhiteMeat("chicken")
  val turkey  = new WhiteMeat("turkey")

  // Food <- Vegetable
  val carrot  = new Vegetable("carrot")
  val pumpkin = new Vegetable("pumpkin")


  // Recipe[Food]: Based on Meat or Vegetable
  val mixRecipe       = new GenericRecipe(List(chicken, carrot, beef, pumpkin))
  // Recipe[Food] <- Recipe[Meat]: Based on any kind of Meat
  val meatRecipe      = new MeatRecipe(List(beef, turkey))
  // Recipe[Food] <- Recipe[Meat] <- Recipe[WhiteMeat]: Based only on WhiteMeat
  val whiteMeatRecipe = new WhiteMeatRecipe(List(chicken, turkey))



  // Chef[WhiteMeat]: Can cook only WhiteMeat
  val giuseppe = new WhiteMeatChef
  giuseppe.cook(whiteMeatRecipe)

  // Chef[WhiteMeat] <- Chef[Meat]: Can cook only Meat
  val alfredo = new MeatChef
  alfredo.cook(meatRecipe)
  alfredo.cook(whiteMeatRecipe)

  // Chef[WhiteMeat]<- Chef[Meat] <- Chef[Food]: Can cook any Food
  val mario = new GenericChef
  mario.cook(mixRecipe)
  mario.cook(meatRecipe)
  mario.cook(whiteMeatRecipe)

  val allFood = new GenericRestaurant(List(mixRecipe), mario)
  val foodParadise = new GenericRestaurant(List(meatRecipe), mario)
  val superFood = new GenericRestaurant(List(whiteMeatRecipe), mario)

  val meat4All = new MeatRestaurant(List(meatRecipe), alfredo)
  val meetMyMeat = new MeatRestaurant(List(whiteMeatRecipe), mario)

  val notOnlyChicken = new WhiteMeatRestaurant(List(whiteMeatRecipe), giuseppe)
  val whiteIsGood = new WhiteMeatRestaurant(List(whiteMeatRecipe), alfredo)
  val wingsLovers = new WhiteMeatRestaurant(List(whiteMeatRecipe), mario)

}


trait Food {
  def name: String
}

class Meat(val name: String) extends Food

class Vegetable(val name: String) extends Food

class WhiteMeat(override val name: String) extends Meat(name)

trait Recipe[+A] {
  def name: String

  def ingredients: List[A]
}

case class GenericRecipe(ingredients: List[Food]) extends Recipe[Food] {
  override def name: String = s"Generic Recipe based on ${ingredients.map(_.name)}"
}

case class MeatRecipe(ingredients: List[Meat]) extends Recipe[Meat] {
  override def name: String = s"Meat Recipe based on ${ingredients.map(_.name)}"
}

case class WhiteMeatRecipe(ingredients: List[WhiteMeat]) extends Recipe[WhiteMeat] {
  override def name: String = s"Meat Recipe based on ${ingredients.map(_.name)}"
}

trait Chef[-A] {
  def specialization: String

  def cook(recipe: Recipe[A]): String
}

class GenericChef extends Chef[Food] {

  val specialization = "All food"

  override def cook(recipe: Recipe[Food]): String = s"I made a ${recipe.name}"
}

class MeatChef extends Chef[Meat] {

  val specialization = "Meat"

  override def cook(recipe: Recipe[Meat]): String = s"I made a ${recipe.name}"
}

class WhiteMeatChef extends Chef[WhiteMeat] {

  override val specialization = "White meat"

  def cook(recipe: Recipe[WhiteMeat]): String = s"I made a ${recipe.name}"
}

trait Restaurant[A] {
  def menu: List[Recipe[A]]
  def chef: Chef[A]

  def cookMenu: List[String] = menu.map(chef.cook)
}


case class GenericRestaurant(menu: List[Recipe[Food]], chef: Chef[Food]) extends Restaurant[Food]
case class MeatRestaurant(menu: List[Recipe[Meat]], chef: Chef[Meat]) extends Restaurant[Meat]
case class WhiteMeatRestaurant(menu: List[Recipe[WhiteMeat]], chef: Chef[WhiteMeat]) extends Restaurant[WhiteMeat]
