package bowling.v1

object BowlingScore extends App {
  val game = new Game(
    List(
      Frame.open(4, 2),
      Frame.spare(5),
      Frame.open(8, 1),
      Frame.Strike,
      Frame.Strike,
      Frame.open(6, 3),
      Frame.spare(5),
      Frame.strike,
      Frame.open(8, 0)
    ),
    FinalFrame.spare(5, 10)
  )

  println(game.score)
}

sealed trait Frame extends Product with Serializable
object Frame {
  case object Strike extends Frame
  final case class Spare(roll: Int) extends Frame
  final case class Open(roll1: Int, roll2: Int) extends Frame

  val strike: Frame = Strike

  def spare(roll: Int): Frame =
    if (roll < 10 && roll >= 0) Spare(roll)
    else
      throw new IllegalArgumentException(
        s"Cannot construct a spare frame from a roll of $roll"
      )

  def open(roll1: Int, roll2: Int): Frame = {
    val total = roll1 + roll2
    if (roll1 >= 0 && roll2 >= 0 && total < 10) Open(roll1, roll2)
    else
      throw new IllegalArgumentException(
        s"Cannot construct an open frame from rolls $roll1 and $roll2"
      )
  }
}

sealed trait FinalFrame extends Product with Serializable
object FinalFrame {
  final case class Strike(bonus1: Int, bonus2: Int) extends FinalFrame
  final case class Spare(roll: Int, bonus: Int) extends FinalFrame
  final case class Open(roll1: Int, roll2: Int) extends FinalFrame

  def strike(bonus1: Int, bonus2: Int): FinalFrame =
    if (bonus1 >= 0 && bonus1 <= 10 && bonus2 >= 0 && bonus2 <= 10)
      Strike(bonus1, bonus2)
    else
      throw new IllegalArgumentException(
        s"Cannot construct a strike final frame with bonus rolls $bonus1 and $bonus2"
      )

  def spare(roll: Int, bonus: Int): FinalFrame =
    if (roll >= 0 && roll < 10 && bonus >= 0 && bonus <= 10)
      Spare(roll, bonus)
    else
      throw new IllegalArgumentException(
        s"Cannot construct a spare final frame with roll $roll and bonus roll $bonus"
      )

  def open(roll1: Int, roll2: Int): FinalFrame =
    if (roll1 >= 0 && roll2 >= 0 && (roll1 + roll2) < 10)
      Spare(roll1, roll2)
    else
      throw new IllegalArgumentException(
        s"Cannot construct a open final frame with rolls $roll1 and $roll2"
      )
}
object Game {
  final case class State(
      score: Int,
      pending: Option[Pending]
  ) {
    def next(additionalScore: Int, newPending: Option[Pending]): State =
      {
      println(additionalScore + score)
        this.copy(
          score = score + additionalScore,
          pending = newPending
        )
      }
  }

  sealed trait Pending extends Product with Serializable
  case object Strike extends Pending
  case object StrikeAndStrike extends Pending
  case object Spare extends Pending

  val initialState = State(0, None)
}
final case class Game(frames: List[Frame], finalFrame: FinalFrame) {
  import Game._

  def score: Int = {
    val state =
      frames
        .foldLeft(initialState) { (state: State, frame: Frame) =>
          state.pending match {
            case Some(value) =>
              value match {
                case Strike =>
                  frame match {
                    case Frame.Strike =>
                      state.next(0, Some(StrikeAndStrike))

                    case Frame.Spare(roll) =>
                      state.next(10 + 10, Some(Spare))

                    case Frame.Open(roll1, roll2) =>
                      state.next(roll1 + roll2 + (10 + roll1 + roll2), None)
                  }

                case StrikeAndStrike =>
                  frame match {
                    case Frame.Strike =>
                      state.next(30, Some(StrikeAndStrike))

                    case Frame.Spare(roll) =>
                      state.next(10 + 10 + roll + (10 + 10), Some(Spare))

                    case Frame.Open(roll1, roll2) =>
                      state.next(10 + 10 + roll1 + (10 + roll1 + roll2), None)
                  }

                case Spare =>
                  frame match {
                    case Frame.Strike =>
                      state.next(10 + 10, Some(Strike))

                    case Frame.Spare(roll) =>
                      state.next(10 + roll, Some(Spare))

                    case Frame.Open(roll1, roll2) =>
                      state.next(10 + roll1 + (roll1 + roll2), None)
                  }
              }
            case None =>
              frame match {
                case Frame.Strike =>
                  state.next(0, Some(Strike))

                case Frame.Spare(roll) =>
                  state.next(0, Some(Spare))

                case Frame.Open(roll1, roll2) =>
                  state.next(roll1 + roll2, None)
              }
          }
        }

    state.pending match {
      case Some(value) =>
        value match {
          case Strike =>
            finalFrame match {
              case FinalFrame.Strike(bonus1, bonus2) =>
                state.score + (10 + 10 + bonus1) + (10 + bonus1 + bonus2)
              case FinalFrame.Spare(roll, bonus) =>
                state.score + (10 + 10) + (10 + bonus)
              case FinalFrame.Open(roll1, roll2) =>
                state.score + (10 + roll1 + roll2) + (roll1 + roll2)
            }
          case StrikeAndStrike =>
            finalFrame match {
              case FinalFrame.Strike(bonus1, bonus2) =>
                state.score + 30 + (10 + 10 + bonus1) + (10 + bonus1 + bonus2)
              case FinalFrame.Spare(roll, bonus) =>
                state.score + (20 + roll) + (10 + roll) + (10 + bonus)
              case FinalFrame.Open(roll1, roll2) =>
                state.score + (20 + roll1) + (10 + roll1 + roll2) + (roll1 + roll2)
            }
          case Spare =>
            finalFrame match {
              case FinalFrame.Strike(bonus1, bonus2) =>
                state.score + 20 + (10 + bonus1 + bonus2)
              case FinalFrame.Spare(roll, bonus) =>
                state.score + (10 + roll) + (10 + bonus)
              case FinalFrame.Open(roll1, roll2) =>
                state.score + (10 + roll1) + (roll1 + roll2)
            }
        }
      case None =>
        finalFrame match {
          case FinalFrame.Strike(bonus1, bonus2) =>
            state.score + (10 + bonus1 + bonus2)
          case FinalFrame.Spare(roll, bonus) =>
            state.score + (10 + bonus)
          case FinalFrame.Open(roll1, roll2) =>
            state.score + (roll1 + roll2)
        }
    }
  }
}
