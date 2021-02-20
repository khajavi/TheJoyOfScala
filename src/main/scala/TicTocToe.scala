import io.circe
import monadicerror.Json

object TicTocToe {
  sealed trait Action
  object Action {
    case object X extends Action
    case object O extends Action
  }

  sealed trait Player
  object Player {
    case object X extends Player
    case object O extends Player
  }

  sealed trait State
  object State {
    case object NotStarted extends State
    case object Playing extends State
    case object Finished extends State
  }

  type Board = Array[Array[Action]]
  case class Game[S <: State](state: S, board: Board)
  type Cell = (Int, Int)

  trait PlayGround {

    /**
      * Who is the winner?
      */
    def winner[S <: State.Finished.type](game: Game[S]): Player

    /**
      * Who is the next player?
      */
    def nextPlayer(board: Board): Player

    //FIXME: who start the game?
    //FIXME: pick shouldn't work on Finished state
//    def pick[S](game: Game[S]): Cell
  }

}

object DeepMerge extends App {
  val j1 = circe.Json.obj(("foo" -> circe.Json.fromString("Hello")))
  val j2 = circe.Json.obj(("bar" -> circe.Json.fromString("Milad")))

  println(j1.deepMerge(j2))
}
