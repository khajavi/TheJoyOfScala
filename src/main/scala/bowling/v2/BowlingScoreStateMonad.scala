package bowling.v2

import bowling.v2.Game.FrameStatus
import bowling.v2.Game.FrameStatus.Pending
import cats.data.State
import cats.effect.IOApp

object BowlingScoreStateMonad extends App {
  val res = for {
    finalFrame <- FinalFrame.strike(10, 10)
    game = new Game((1 to 9).map(_ => Frame.strike).toList, finalFrame)
  } yield (game)
  println(res.map(_.score))
}

sealed trait Error
case class IllegalSpareFrame(roll: Int) extends Error
case class IllegalFinalSpareFrame(roll: Int, bonus: Int) extends Error
case class IllegalOpenFrame(roll1: Int, roll2: Int) extends Error
case class IllegalStrikeFrame(bonus1: Int, bonus2: Int) extends Error

sealed trait Frame extends Product with Serializable
object Frame {
  case object Strike extends Frame
  final case class Spare(roll: Int, rol2: Int) extends Frame
  final case class Open(rol1: Int, rol2: Int) extends Frame

  val strike: Frame = Strike

  def spare(roll: Int, rol2: Int): Either[Error, Frame] =
    if (roll < 10 && roll >= 0 && rol2 < 10 && roll >= 0 && (roll + rol2) == 10)
      Right(Spare(roll, rol2))
    else
      Left(IllegalSpareFrame(roll))

  def open(roll1: Int, roll2: Int): Either[Error, Frame] = {
    val total = roll1 + roll2
    if (roll1 >= 0 && roll2 >= 0 && total < 10) Right(Open(roll1, roll2))
    else
      Left(IllegalOpenFrame(roll1, roll2))
  }

}

sealed trait FinalFrame extends Product with Serializable
object FinalFrame {
  final case class Strike(bonus1: Int, bonus2: Int) extends FinalFrame
  final case class Spare(rol1: Int, rol2: Int, bonus: Int) extends FinalFrame
  final case class Open(rol1: Int, rol2: Int) extends FinalFrame

  def strike(bonus1: Int, bonus2: Int): Either[Error, FinalFrame] =
    if (bonus1 >= 0 && bonus1 <= 10 && bonus2 >= 0 && bonus2 <= 10)
      Right(Strike(bonus1, bonus2))
    else
      Left(IllegalStrikeFrame(bonus1, bonus2))

  def spare(
      roll: Int,
      rol2: Int,
      bonus: Int
  ): Either[IllegalFinalSpareFrame, FinalFrame] =
    if (roll >= 0 && roll < 10 && bonus >= 0 && bonus <= 10)
      Right(Spare(roll, rol2, bonus))
    else
      Left(IllegalFinalSpareFrame(roll, bonus))

  def open(rol1: Int, rol2: Int): Either[IllegalOpenFrame, FinalFrame] =
    if (rol1 >= 0 && rol2 >= 0 && (rol1 + rol2) < 10)
      Right(Open(rol1, rol2))
    else
      Left(IllegalOpenFrame(rol1, rol2))
}
object Game {
  final case class GameState(
      score: Int,
      status: FrameStatus
  ) {
    def next(additionalScore: Int, newStatus: FrameStatus): GameState =
      this.copy(
        score = score + additionalScore,
        status = newStatus
      )
  }

  sealed trait FrameStatus extends Product with Serializable
  object FrameStatus {
    sealed trait PendingFrame extends FrameStatus
    object Pending {
      case object PendingStrikeFrame extends PendingFrame
      case object PendingStrikeAndStrikeFrame extends PendingFrame
      case object PendingSpareFrame extends PendingFrame
    }
    case object FinishedFrame extends FrameStatus
  }

  val initialGameState = GameState(0, FrameStatus.FinishedFrame)

}

final case class Game(frames: List[Frame], finalFrame: FinalFrame) {
  import Game._

  def score: Int = {
    val state: GameState = frames.foldLeft(initialGameState) {
      (state: GameState, frame: Frame) =>
        state.status match {
          case pending: FrameStatus.PendingFrame =>
            pending match {
              case FrameStatus.Pending.PendingStrikeFrame =>
                frame match {
                  case Frame.Strike =>
                    state.copy(
                      state.score,
                      FrameStatus.Pending.PendingStrikeAndStrikeFrame
                    )
                  case Frame.Spare(rol1, rol2) =>
                    state.copy(
                      state.score + 10 + rol1 + rol2,
                      FrameStatus.Pending.PendingSpareFrame
                    )
                  case Frame.Open(roll1, roll2) =>
                    state.copy(
                      state.score + (10 + roll1 + roll2) + (roll1 + roll2),
                      FrameStatus.FinishedFrame
                    )
                }
              case FrameStatus.Pending.PendingStrikeAndStrikeFrame =>
                frame match {
                  case Frame.Strike =>
                    state.copy(
                      state.score + 30,
                      FrameStatus.Pending.PendingStrikeAndStrikeFrame
                    )
                  case Frame.Spare(rol1, rol2) =>
                    state.copy(
                      state.score + (10 + 10 + rol1) + (10 + rol1 + rol2),
                      FrameStatus.Pending.PendingSpareFrame
                    )
                  case Frame.Open(roll1, roll2) =>
                    state.copy(
                      state.score + (10 + 10 + roll1) + (10 + roll1 + roll2),
                      FrameStatus.FinishedFrame
                    )
                }
              case FrameStatus.Pending.PendingSpareFrame =>
                frame match {
                  case Frame.Strike =>
                    state.copy(
                      state.score + (10 + 10),
                      FrameStatus.Pending.PendingStrikeFrame
                    )
                  case Frame.Spare(roll, rol2) =>
                    state.copy(
                      state.score + (10 + roll),
                      FrameStatus.Pending.PendingSpareFrame
                    )
                  case Frame.Open(roll1, roll2) =>
                    state.copy(
                      state.score + (10 + roll1) + (roll1 + roll2),
                      FrameStatus.FinishedFrame
                    )
                }
            }
          case FrameStatus.FinishedFrame =>
            frame match {
              case Frame.Strike =>
                state.copy(
                  state.score,
                  FrameStatus.Pending.PendingStrikeFrame
                )
              case Frame.Spare(roll, rol2) =>
                state.copy(
                  state.score,
                  FrameStatus.Pending.PendingSpareFrame
                )
              case Frame.Open(roll1, roll2) =>
                state.copy(
                  state.score + roll1 + roll2,
                  FrameStatus.FinishedFrame
                )
            }
        }
    }

    state.status match {
      case frame: FrameStatus.PendingFrame =>
        frame match {
          case Pending.PendingStrikeFrame =>
            finalFrame match {
              case FinalFrame.Strike(bonus1, bonus2) =>
                state.score + (10 + 10 + bonus1) + (10 + bonus1 + bonus2)
              case FinalFrame.Spare(roll, rol2, bonus) =>
                state.score + (10 + roll + rol2) + (roll + rol2 + bonus)
              case FinalFrame.Open(rol1, rol2) =>
                state.score + (10 + rol1 + rol2) + (rol1 + rol2)
            }
          case Pending.PendingStrikeAndStrikeFrame =>
            finalFrame match {
              case FinalFrame.Strike(bonus1, bonus2) =>
                state.score + (10 + 10 + 10) + (10 + 10 + bonus1) + (10 + bonus1 + bonus2)
              case FinalFrame.Spare(rol1, rol2, bonus) =>
                state.score + (10 + 10 + rol1) + (10 + rol1 + rol2) + (rol1 + rol2 + bonus)
              case FinalFrame.Open(rol1, rol2) =>
                state.score + (10 + 10 + rol1) + (10 + rol1 + rol2) + (rol1 + rol2)
            }
          case Pending.PendingSpareFrame =>
            finalFrame match {
              case FinalFrame.Strike(bonus1, bonus2) =>
                state.score + (10 + 10) + (10 + bonus1 + bonus2)
              case FinalFrame.Spare(rol1, rol2, bonus) =>
                state.score + (10 + rol1) + (rol1 + rol2 + bonus)
              case FinalFrame.Open(rol1, rol2) =>
                state.score + (10 + rol1) + (rol1 + rol2)
            }
        }
      case FrameStatus.FinishedFrame =>
        finalFrame match {
          case FinalFrame.Strike(bonus1, bonus2) =>
            state.score + (10 + bonus1 + bonus2)
          case FinalFrame.Spare(rol1, rol2, bonus) =>
            state.score + (rol1 + rol2 + bonus)
          case FinalFrame.Open(rol1, rol2) =>
            state.score + rol1 + rol2
        }
    }
  }

}

object ABCDED extends App {

  type GState[Int] = State[List[Frame], Int]
  type Score = Int

  type BGState[Score] = State[BState, Score]

  case class BState(score: Score, frame: Frame, frameStatus: FrameStatus)
  def func: BGState[Score] = {
    State { state =>
      state.frameStatus match {
        case pending: FrameStatus.PendingFrame =>
          pending match {
            case Pending.PendingStrikeFrame =>
              state.frame match {
                case Frame.Strike =>
                  (
                    BState(
                      state.score + 0,
                      state.frame,
                      Pending.PendingStrikeAndStrikeFrame
                    ),
                    state.score
                  )
                case _ => throw new Exception
              }
            case Pending.PendingStrikeAndStrikeFrame =>
              state.frame match {
                case Frame.Strike =>
                  (
                    BState(
                      state.score + 30,
                      state.frame,
                      Pending.PendingStrikeAndStrikeFrame
                    ),
                    state.score + 30
                  )
                case Frame.Spare(roll, rol2) => ???
                case Frame.Open(rol1, rol2)  => ???
              }
            case Pending.PendingSpareFrame => ???
          }
        case FrameStatus.FinishedFrame =>
          state.frame match {
            case Frame.Strike =>
              (
                BState(
                  state.score + 0,
                  state.frame,
                  Pending.PendingStrikeFrame
                ),
                state.score + 0
              )
            case Frame.Spare(roll, rol2) => ???
            case Frame.Open(rol1, rol2)  => ???
          }
      }
    }

  }

  val initialState = BState(0, Frame.strike, FrameStatus.FinishedFrame)
//  println(func.run(initialState).value)

  val res = for {
    a <- func
    b <- func
    b <- func
    b <- func
  } yield (b)

  println(res.run(initialState).value)
}

object ABCDEDEFGF extends App {

  type GState[Int] = State[List[Frame], Int]
  type Score = Int

  type BGState[Score] = State[BState, Score]

  case class BState(score: Score, frames: List[Frame], frameStatus: FrameStatus)
  def score: BGState[Score] = {
    State { state =>
      state.frames match {
        case ::(currentFrame, pendingFrames) =>
          state.frameStatus match {
            case pending: FrameStatus.PendingFrame =>
              pending match {
                case Pending.PendingStrikeFrame =>
                  currentFrame match {
                    case Frame.Strike =>
                      (
                        BState(
                          state.score + 0,
                          pendingFrames,
                          Pending.PendingStrikeAndStrikeFrame
                        ),
                        state.score
                      )
                    case _ => throw new Exception
                  }
                case Pending.PendingStrikeAndStrikeFrame =>
                  currentFrame match {
                    case Frame.Strike =>
                      (
                        BState(
                          state.score + 30,
                          pendingFrames,
                          Pending.PendingStrikeAndStrikeFrame
                        ),
                        state.score + 30
                      )
                    case Frame.Spare(roll, rol2) => ???
                    case Frame.Open(rol1, rol2)  => ???
                  }
                case Pending.PendingSpareFrame => ???
              }
            case FrameStatus.FinishedFrame =>
              currentFrame match {
                case Frame.Strike =>
                  (
                    BState(
                      state.score + 0,
                      pendingFrames,
                      Pending.PendingStrikeFrame
                    ),
                    state.score + 0
                  )
                case Frame.Spare(roll, rol2) => ???
                case Frame.Open(rol1, rol2)  => ???
              }
          }
        case Nil => ???
      }
    }
  }

  def pop: BGState[Frame] =
    State { state =>
      state.frames match {
        case ::(head, tail) => (state.copy(frames = tail), head)
        case Nil            => throw new Exception
      }
    }

  def compute: BGState[Score] =
    for {
      _ <- score
      e <- State.get
      res <- if (e.frames.size > 1) compute else score
    } yield (res)

  val initialState =
    BState(0, (1 to 4).map(_ => Frame.strike).toList, FrameStatus.FinishedFrame)
//  println(score.run(initialState).value)

  println(compute.run(initialState).value)

}

object FinalSolution extends App {
  type Score = Int
  type BGState[Score] = State[BState, Score]

  case class BState(score: Score, frames: List[Frame], frameStatus: FrameStatus)

  def score: BGState[Score] = {
    State { state =>
      state.frames match {
        case ::(currentFrame, pendingFrames) =>
          state.frameStatus match {
            case pending: FrameStatus.PendingFrame =>
              pending match {
                case Pending.PendingStrikeFrame =>
                  currentFrame match {
                    case Frame.Strike =>
                      (
                        BState(
                          state.score + 0,
                          pendingFrames,
                          Pending.PendingStrikeAndStrikeFrame
                        ),
                        state.score
                      )
                    case _ => throw new Exception
                  }
                case Pending.PendingStrikeAndStrikeFrame =>
                  currentFrame match {
                    case Frame.Strike =>
                      (
                        BState(
                          state.score + 30,
                          pendingFrames,
                          Pending.PendingStrikeAndStrikeFrame
                        ),
                        state.score + 30
                      )
                    case Frame.Spare(roll, rol2) => ???
                    case Frame.Open(rol1, rol2)  => ???
                  }
                case Pending.PendingSpareFrame => ???
              }
            case FrameStatus.FinishedFrame =>
              currentFrame match {
                case Frame.Strike =>
                  (
                    BState(
                      state.score + 0,
                      pendingFrames,
                      Pending.PendingStrikeFrame
                    ),
                    state.score + 0
                  )
                case Frame.Spare(roll, rol2) => ???
                case Frame.Open(rol1, rol2)  => ???
              }
          }
        case Nil => ???
      }
    }
  }

  def pop: BGState[Frame] =
    State { state =>
      state.frames match {
        case ::(head, tail) => (state.copy(frames = tail), head)
        case Nil            => throw new Exception
      }
    }

  def compute: BGState[Score] =
    for {
      _ <- score
      e <- State.get
      res <- if (e.frames.size > 1) compute else score
    } yield (res)

  val initialState =
    BState(0, (1 to 4).map(_ => Frame.strike).toList, FrameStatus.FinishedFrame)

  println(compute.run(initialState).value)

}

//object AU extends App {
//  type Score = Int
//  case class FrameState(score: Score, status: FrameStatus)
//  type BGState[Score] = State[FrameState, Score]
//  
//  def computeRoll(frame: Frame): BGState[Score] = State { fs =>
//    fs.status match {
//      case frame: FrameStatus.PendingFrame => ???
//      case FrameStatus.FinishedFrame => ???
//    }
//  }
//  
//  def popFrame: BGState[List[Frame]] = State { s =>
//  }
//  
//
//}
