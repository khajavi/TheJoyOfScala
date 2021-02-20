package typeclass

import cats.effect.IO
import typeclass.TryTwo.{EMAConfig, SMAConfig}

object TryOne {
  object TypeClassWithInheritanceExample extends App {
    val list: List[Config] = List(SMAConfig(1), EMAConfig(2))
    def print[T](a: T)(implicit enc: Encoder[T]): Unit = {
      println(enc.encode(a))
    }

    list.map(print[Config])
  }

  trait Encoder[T] {
    def encode(a: T): String
  }

  sealed trait Config
  case class SMAConfig(period: Int) extends Config
  case class EMAConfig(period: Int) extends Config

  object Encoder {
    implicit val smaEncoder: Encoder[SMAConfig] = (a: SMAConfig) => a.toString

    implicit val emaEncoder: Encoder[EMAConfig] = (a: EMAConfig) => a.toString

    implicit val configEncoder: Encoder[Config] = {
      case sma: SMAConfig => smaEncoder.encode(sma)
      case ema: EMAConfig => emaEncoder.encode(ema)
    }
  }
}

object TryTwo extends App {
  trait IndicatorBuilder[C <: Config, O <: Output] {
    def indicator[F[_]]: C => fs2.Stream[F, O]
  }

  def indicator[F[_], C <: Config, O <: Output](config: C)(implicit
      builder: IndicatorBuilder[C, O]
  ): fs2.Stream[F, O] = {
    builder.indicator(config)
  }

  sealed trait Config extends Product
  case class SMAConfig(period: Int) extends Config
  case class EMAConfig(period: Int) extends Config

  sealed trait Output
  case class SMAOutput(sma: Int) extends Output
  case class EMAOutput(ema: String) extends Output

  implicit val sma: IndicatorBuilder[SMAConfig, SMAOutput] =
    new IndicatorBuilder[SMAConfig, SMAOutput] {
      override def indicator[F[_]]: SMAConfig => fs2.Stream[F, SMAOutput] =
        sma => fs2.Stream[F, SMAOutput](SMAOutput(sma.period))
    }
  implicit object ema extends IndicatorBuilder[EMAConfig, EMAOutput] {
    override def indicator[F[_]]: EMAConfig => fs2.Stream[F, EMAOutput] =
      ema => fs2.Stream[F, EMAOutput](EMAOutput(ema.period.toString))
  }

  implicit val all: IndicatorBuilder[Config, Output] =
    new IndicatorBuilder[Config, Output] {
      override def indicator[F[_]]: Config => fs2.Stream[F, Output] = {
        case c: SMAConfig =>
          implicitly[IndicatorBuilder[SMAConfig, SMAOutput]].indicator[F](c)
        case c: EMAConfig =>
          implicitly[IndicatorBuilder[EMAConfig, EMAOutput]].indicator[F](c)
      }
    }

  val l =
    List(SMAConfig(1), EMAConfig(2)).map(x =>
      indicator[IO, Config, Output](x)(all)
    )

  indicator[IO, SMAConfig, SMAOutput](SMAConfig(1))
}

object TryThree extends App {
  trait IndicatorBuilder[C <: Config] {
    type O <: Output
    def indicator[F[_]]: C => fs2.Stream[F, O]
  }

  def indicator[F[_], C <: Config](config: C)(implicit
      builder: IndicatorBuilder[C]
  ): fs2.Stream[F, builder.O] = {
    builder.indicator(config)
  }

  sealed trait Config extends Product
  case class SMAConfig(period: Int) extends Config
  case class EMAConfig(period: Int) extends Config

  sealed trait Output
  case class SMAOutput(sma: Int) extends Output
  case class EMAOutput(ema: String) extends Output

  implicit val sma: IndicatorBuilder[SMAConfig] =
    new IndicatorBuilder[SMAConfig] {
      override type O = SMAOutput
      override def indicator[F[_]]: SMAConfig => fs2.Stream[F, O] =
        sma => fs2.Stream[F, O](SMAOutput(sma.period))
    }
  implicit object ema extends IndicatorBuilder[EMAConfig] {
    override type O = EMAOutput
    override def indicator[F[_]]: EMAConfig => fs2.Stream[F, O] =
      ema => fs2.Stream[F, O](EMAOutput(ema.period.toString))
  }

  implicit val all: IndicatorBuilder[Config] =
    new IndicatorBuilder[Config] {
      override type O = Output
      override def indicator[F[_]]: Config => fs2.Stream[F, Output] = {
        case c: SMAConfig =>
          implicitly[IndicatorBuilder[SMAConfig]].indicator[F](c)
        case c: EMAConfig =>
          implicitly[IndicatorBuilder[EMAConfig]].indicator[F](c)
      }
    }

  val l =
    List(SMAConfig(1), EMAConfig(2)).map(x => indicator[IO, Config](x)(all))

//  indicator[IO, SMAConfig](SMAConfig(1))
}

object Abcde extends App {
  type Conf = SMAConfig with EMAConfig

  val sma: SMAConfig = ???
  val ema: EMAConfig = ???

//  val f: Conf = ema
}

object Solution extends App {
  def romanToInt(s: String): Int =
    s match {
      case "I" => 1
      case "V" => 5
      case "X" => 10
      case "L" => 50
      case "C" => 100
      case "D" => 500
      case "M" => 1000
    }

  romanToInt("IV")
}


