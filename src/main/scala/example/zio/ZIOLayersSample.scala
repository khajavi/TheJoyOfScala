package example.zio

import zio.{Has, UIO, ULayer, ZIO, ZLayer}

object ZIOLayersSample extends App {
  def show(message: String) = Show.display(message)

  trait Show {
    def display(message: String): UIO[Unit]
  }

  // Helper
  object Show {
    def display(message: String): ZIO[Has[Show], Nothing, Unit] = {
      ZIO.accessM[Has[Show]](_.get.display(message))
      //      ZIO.service[Show].flatMap(_.display(message))
    }

  }

  val layer1: ULayer[Has[Show]] = ZLayer.succeed(new Show {
    override def display(message: String): UIO[Unit] =
      ZIO.effectTotal(println(message))
  })

  type Lines = List[String]

  trait LinesPersistence {
    def update(toState: Lines => Lines): UIO[Unit]

    def get: UIO[Lines]
  }

  object LinesPersistence {

    case class LinesPersistenceUsingRef(lines: zio.Ref[List[String]]) extends LinesPersistence {
      override def update(toState: Lines => Lines): UIO[Unit] = lines.update(toState)

      override def get: UIO[Lines] = lines.get
    }

    val layer: ZLayer[Any, Nothing, Has[LinesPersistence]] =
      zio.Ref.make(List.empty[String]).map(LinesPersistenceUsingRef).toLayer
  }

  case class ShowTest(lines: LinesPersistence) extends Show {
    override def display(message: String): UIO[Unit] =
      lines.update(_ :+ message)
  }

  def layer2: ZLayer[Has[LinesPersistence], Nothing, Has[Show]] = {
    ZLayer.fromService { lp =>
      ShowTest(lp)
    }
  }

  val showUsingFirstLayer  = show("Hello, World!").provideLayer(layer1)
  val showUsingSecondLayer = for {
    state <- ZIO.service[LinesPersistence]
    _ <- show("Hi") *> show("Folan")
    lines <- state.get
  } yield lines

  println(zio.Runtime.global.unsafeRun(
    showUsingSecondLayer.provideLayer(LinesPersistence.layer >+> layer2)))
}
