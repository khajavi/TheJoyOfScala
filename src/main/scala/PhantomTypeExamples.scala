

object Try1 extends App {

  class Hacker {
    def hackOn: Hacker = {
      println("Hacking, Hacking, Hacking!")
      new Hacker
    }

    def drinkCaffe: Hacker = {
      println("Slurp ...")
      new Hacker
    }
  }


  val hacker = new Hacker
  hacker.hackOn.hackOn
}

object Try2 extends App {

  class Hacker[S <: Hacker.State] {

    import Hacker.State

    def hackOn: Hacker[State.Decaffeinated] = {
      println("Hacking, Hacking, Hacking!")
      new Hacker
    }

    def drinkCaffe: Hacker[State.Caffeinated] = {
      println("Slurp ...")
      new Hacker
    }
  }

  object Hacker {

    sealed trait State

    object State {

      sealed trait Caffeinated extends State

      sealed trait Decaffeinated extends State

    }

  }

  val hacker = new Hacker[Hacker.State.Caffeinated]
  hacker.hackOn
  hacker.hackOn.hackOn // This should not compile, but it still does!

}


object Try3 extends App {

  class Hacker[S <: Hacker.State] {

    import Hacker.State

    def hackOn[T >: S <: State.Caffeinated]: Hacker[State.Decaffeinated] = {
      println("Hacking, Hacking, Hacking!")
      new Hacker
    }

    def drinkCaffe[T >: S <: State.Decaffeinated]: Hacker[State.Caffeinated] = {
      println("Slurp ...")
      new Hacker
    }
  }

  object Hacker {

    sealed trait State

    object State {

      sealed trait Caffeinated extends State

      sealed trait Decaffeinated extends State

    }

  }

  val hacker = new Hacker[Hacker.State.Caffeinated]
  hacker.hackOn
//  hacker.hackOn.hackOn // This should not compile

}


object Try4 extends App {

  class Hacker[S <: Hacker.State] {

    import Hacker._

    def hackOn(implicit ev: =:=[S, State.Caffeinated]): Hacker[State.Decaffeinated] = {
      println("Hacking, Hacking, Hacking!")
      new Hacker
    }

    def drinkCaffe(implicit ev: S =:= State.Decaffeinated): Hacker[State.Caffeinated] = {
      println("Slurp ...")
      new Hacker
    }
  }

  object Hacker {

    sealed trait State

    object State {

      sealed trait Caffeinated extends State

      sealed trait Decaffeinated extends State

    }

  }

  val hacker = new Hacker[Hacker.State.Caffeinated]

  hacker.hackOn
  //    hacker.hackOn.hackOn  // This should not compile

}


object Try5 extends App {

  class Hacker[S <: Hacker.State] {

    import Hacker._

    type IsCaffeinated[T] = =:=[T, State.Caffeinated]
    type IsDecaffeinated[T] = =:=[T, State.Decaffeinated]

    def hackOn(implicit ev: IsCaffeinated[S]): Hacker[State.Decaffeinated] = {
      println("Hacking, Hacking, Hacking!")
      new Hacker
    }

    def drinkCaffe(implicit ev: IsDecaffeinated[S]): Hacker[State.Caffeinated] = {
      println("Slurp ...")
      new Hacker
    }
  }

  object Hacker {

    sealed trait State

    object State {

      sealed trait Caffeinated extends State

      sealed trait Decaffeinated extends State

    }

  }

  val hacker = new Hacker[Hacker.State.Caffeinated]

  hacker.hackOn
  //      hacker.hackOn.hackOn  // This should not compile
}
