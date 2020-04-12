import cats.tagless.{Derive, FunctorK}
import cats.~>

object FunctorKExample extends App {
  type EitherNone[T] = Either[None.type, T]
  val higherKindedTransformation: Option ~> EitherNone = new (Option ~> EitherNone) {
    override def apply[A](fa: Option[A]): EitherNone[A] = fa match {
      case Some(a) => Right(a)
      case None => Left(None)
    }
  }
  val service                                          = new Service[Option] {
    override def getNumber: Option[Int] = Some(3)
  }

  implicit val functorK: FunctorK[Service] = Derive.functorK[Service]
  val transformed = FunctorK[Service].mapK(service)(higherKindedTransformation)

  println(transformed.getNumber)
}


trait Service[F[_]] {
  def getNumber: F[Int]
}

