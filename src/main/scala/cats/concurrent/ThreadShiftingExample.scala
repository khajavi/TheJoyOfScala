package cats.concurrent

import cats.effect.{ExitCode, IOApp}

object ThreadShiftingExample extends IOApp {

  import java.util.concurrent.Executors

  import cats.effect.IO

  import scala.concurrent.ExecutionContext

  val cachedThreadPool = Executors.newCachedThreadPool()
  val BlockingFileIO   = ExecutionContext.fromExecutor(cachedThreadPool)
  implicit val Main = ExecutionContext.global

  val ioa: IO[Unit] =
    for {
      _ <- IO(println("Enter your name: "))
      _ <- IO.shift(BlockingFileIO)
      name <- IO(scala.io.StdIn.readLine())
      _ <- IO.shift(Main)
      _ <- IO(println(s"Welcome $name!"))
      _ <- IO(cachedThreadPool.shutdown())
    } yield ()

  override def run(args: List[String]): IO[ExitCode] = ioa.as(ExitCode.Success)
}
