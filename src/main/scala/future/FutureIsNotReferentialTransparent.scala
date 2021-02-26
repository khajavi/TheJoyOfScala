package future

import cats.effect
import cats.effect.{ExitCode, IOApp}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

object FutureIsNotReferentialTransparent extends App {
  def firstTry(): Unit = {
    val hello       = Future {
      println("Hello, World")
    }
    val computation = for {
      _ <- hello
      _ <- hello
    } yield ()
    Await.result(computation, Duration.Inf)
  }

  def secondTry(): Unit = {
    val computation = for {
      _ <- Future {
        println("Hello, World")
      }
      _ <- Future {
        println("Hello, World")
      }
    } yield ()
    Await.result(computation, Duration.Inf)
  }

  firstTry()
  secondTry()
}

object RetryOnUserInputIOVersion extends IOApp {

  def readNumber: effect.IO[Int] = for {
    _ <- effect.IO(println("input a number: "))
    number <- effect.IO(scala.io.StdIn.readInt())
    res <- if (number >= 0) effect.IO(number) else readNumber
  } yield res

  override def run(args: List[String]): effect.IO[ExitCode] = readNumber.as(ExitCode.Success)
}


object RetryOnUserInputFutureVersion extends App {
  def readNumber: Future[Int] = for {
    _ <- Future(println("input a number: "))
    number <- Future(scala.io.StdIn.readInt())
    res <- if (number >= 0) Future(number) else readNumber
  } yield res

  readNumber.onComplete {
    case Failure(exception) => println(exception.getMessage)
    case Success(value) => println(s"positive number: $value")
  }
}
