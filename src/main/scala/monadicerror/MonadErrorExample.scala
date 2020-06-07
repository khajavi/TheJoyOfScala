package monadicerror

import cats.MonadError
import cats.implicits.{catsStdInstancesForEither, catsStdInstancesForTry}

import scala.util.Try

case class Json()

object MonadErrorExample1 extends App {
  val content = "{}"

  def toJson[F[_]](str: String)(implicit M: MonadError[F, Throwable]): F[Json] = {
    if (true) M.pure(new Json)
    else M.raiseError(new Exception("failed to parse"))
  }

  val parsedTry     = toJson[Try](content)
  val parsedEither1 = toJson[Lambda[A => Either[Throwable, A]]](content)

  trait EitherF {
    type R[A] = Either[Throwable, A]
  }

  val parsedEither2 = toJson[EitherF#R](content)
  //  val parsedEither4 = toJson[EitherF[String]](content)
}


object MonadErrorExample2 extends App {
  val content = "{}"

  trait GenericError[A] {
    def errorFromString(str: String): A

    def errorFromThrowable(thr: Throwable): A
  }

  def toJson[F[_], E](str: String)(implicit M: MonadError[F, E], E: GenericError[E]): F[Json] = {
    if (true) M.pure(new Json)
    else M.raiseError(E.errorFromString("failed to parse"))
  }

  implicit val throwableGenericError = new GenericError[Throwable] {
    override def errorFromString(str: String): Throwable = new Exception(str)

    override def errorFromThrowable(thr: Throwable): Throwable = thr
  }


  implicit val stringGenericError = new GenericError[String] {
    override def errorFromString(str: String): String = str

    override def errorFromThrowable(thr: Throwable): String = thr.getMessage
  }
  val parsedTry     = toJson[Try, Throwable](content)
  val parsedEither1 = toJson[Lambda[A => Either[String, A]], String](content)
  val parsedEither2 = toJson[Either[String, *], String](content)
  val parsedEither3 = toJson[Either[String, ?], String](content)
  val parsedEither4 = toJson[λ[A => Either[String, A]], String](content)

  trait EitherF {
    type R[A] = Either[Throwable, A]
  }

  val parsedEither5 = toJson[EitherF#R, Throwable](content)
  //  val parsedEither4 = toJson[EitherF[String]](content)
}
