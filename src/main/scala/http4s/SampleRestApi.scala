package http4s

import cats.data.Kleisli
import cats.effect.{ExitCode, IO, IOApp}
import http4s.BookModels.{Id, Title}
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder

import scala.collection.mutable


object SampleRestApi {

  case class Hut(name: String)

  case class HutWithId(id: String, name: String)

}

object BookModels {
  type Title = String
  type Id = String
}

case class Book(title: Title)

case class BookWithId(id: Id, title: Title)

trait BookRepo[F[_]] {
  def get(id: Id): F[Option[BookWithId]]

  def getAll: F[List[BookWithId]]

  def add(book: Book): F[Id]
}

object DummyBookRepo extends BookRepo[cats.Id] {
  val storage: collection.mutable.Map[Id, Book] = mutable.HashMap("3" -> Book("Foo"), "2" -> Book("Bar"))

  override def get(id: Id): cats.Id[Option[BookWithId]] = {
    storage.get(id).map(b => BookWithId(id, b.title))
  }

  override def getAll: cats.Id[List[BookWithId]] =
    (storage.toList.map { case (id, book) => BookWithId(id, book.title) })

  override def add(book: Book): cats.Id[Id] = {
    storage.addOne("3", book)
    "3"
  }
}


object Main extends IOApp {

  val dsl = new Http4sDsl[IO] {}

  import dsl._

  val httpRoutes: Kleisli[IO, Request[IO], Response[IO]] = Router(
    "/" ->
      HttpRoutes.of[IO] {
        case _@GET -> Root / "books" =>
          IO(DummyBookRepo.getAll).flatMap(b => Ok(b))
        case req@POST -> Root / "book" =>
          req.decode[Book] { b =>
            IO(DummyBookRepo.add(b)).flatMap(b => Ok(b))
          }
      }
  ).orNotFound

  override def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO](scala.concurrent.ExecutionContext.global)
      .bindHttp(9000, "0.0.0.0")
      .withHttpApp(httpRoutes)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
}
