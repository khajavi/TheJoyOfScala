package aux

import cats.effect.IO
import fs2.Pipe

/**
  * @author Milad Khajavi <khajavi@gmail.com>.
  */
object IndicatorExample extends App {

  sealed trait Indicator {
    type Config
    type Output
  }

  object Indicator {
    type Aux[C, O] = Indicator {
      type Config = C
      type Output = O
    }
  }

  implicit object SMA extends Indicator {
    override type Config = Int
    override type Output = String
  }

  trait IndicatorBuilder[C, O] {
    def build[F[_]]: C => Pipe[F, Double, O]
  }

  def indicatorPipe[F[_], C, O](in: Indicator.Aux[C, O])(implicit
      builder: IndicatorBuilder[C, O]
  ): C => Pipe[F, Double, in.Output] = builder.build

  implicit object smabuilder extends IndicatorBuilder[Int, String] {
    override def build[F[_]]: Int => Pipe[F, Double, String] =
      _ => _.map(_.toString)
  }

  indicatorPipe[IO, Int, String](SMA)

}

object IndicatorExample2 extends App {

  sealed trait Indicator {
    type Config
    type Output
  }

  object Indicator {
    type Aux[C, O] = Indicator {
      type Config = C
      type Output = O
    }
  }

  implicit object SMA extends Indicator {
    override type Config = Int
    override type Output = String
  }

  trait IndicatorBuilder[C, O] {
    def build[F[_]]: C => Pipe[F, Double, O]
  }
  object IndicatorBuilder {
    class Builder[F[_]] {
      def indicatorPipe[C, O](in: Indicator.Aux[C, O])(implicit
          builder: IndicatorBuilder[C, O]
      ): C => Pipe[F, Double, in.Output] = builder.build
    }
    def apply[F[_]] = new Builder[F]
  }

  def indicatorPipe[F[_], C, O](in: Indicator.Aux[C, O])(implicit
      builder: IndicatorBuilder[C, O]
  ): C => Pipe[F, Double, in.Output] = builder.build

  implicit object smabuilder extends IndicatorBuilder[Int, String] {
    override def build[F[_]]: Int => Pipe[F, Double, String] =
      _ => _.map(_.toString)
  }

  indicatorPipe[IO, Int, String](SMA)

  IndicatorBuilder[IO].indicatorPipe(SMA)

}

object IndicatorExample3 extends App {

  sealed trait Indicator[C, O]
  final case object SMA extends Indicator[Int, String]

  trait IndicatorBuilder[C, O] {
    def build[F[_]]: C => Pipe[F, Double, O]
  }

  object IndicatorBuilder {
    class Builder[F[_]] {
      def build[C, O](in: Indicator[C, O])(implicit
          builder: IndicatorBuilder[C, O]
      ): C => Pipe[F, Double, O] = {
        val _ = in
        builder.build
      }
    }
    def apply[F[_]] = new Builder[F]
  }

  def indicatorPipe[F[_], C, O](implicit
      builder: IndicatorBuilder[C, O]
  ): C => Pipe[F, Double, O] = builder.build

  implicit object smabuilder extends IndicatorBuilder[Int, String] {
    override def build[F[_]]: Int => Pipe[F, Double, String] =
      _ => _.map(_.toString)
  }

  IndicatorBuilder[IO].build(SMA)
}
