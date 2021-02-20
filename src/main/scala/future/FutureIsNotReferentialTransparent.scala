package future

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object FutureIsNotReferentialTransparent extends App {
  def firstTry(): Unit = {
    val hello = Future { println("Hello, World") }
    val computation = for {
      _ <- hello
      _ <- hello
    } yield ()
    Await.result(computation, Duration.Inf)
  }

  def secondTry(): Unit = {
    val computation = for {
      _ <- Future { println("Hello, World") }
      _ <- Future { println("Hello, World") }
    } yield ()
    Await.result(computation, Duration.Inf)
  }
  
  firstTry()
  secondTry()
}
