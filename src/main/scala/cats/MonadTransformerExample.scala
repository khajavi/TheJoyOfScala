package cats

import cats.data.OptionT
import cats.effect.{ExitCode, IO, IOApp}

object MonadTransformerExample extends IOApp {
  val userMaybe: OptionT[IO, String] = OptionT.fromOption(Some("1"))

  override def run(args: List[String]): IO[ExitCode] = {
    for {
      user <- userMaybe.value
      _ <- IO(println(user))
    } yield ExitCode.Success
  }
}
