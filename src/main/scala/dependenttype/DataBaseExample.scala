package dependenttype

import cats.effect.Sync
import dependenttype.Database.Key
import fs2.{Pipe, Pure}

import scala.collection.mutable
import scala.language.implicitConversions

object DataBaseExample extends App {
  val db = new Database

  object keys {

    trait TypedKey[F] extends Key {
      override type Value = F
    }

    def intkey(name: String) = new Key(name) with TypedKey[Int]

    def doubleKey(name: String) = new Key(name) with TypedKey[Double]
  }

  import keys._

  db.set(intkey("foo"))(3)
  db.set(doubleKey("double"))(2.4d)
  println(db.get(intkey("foo")))
  val a: Option[Double] = db.get(doubleKey("double"))
  println(a)

}

class Database {

  import Database.Key

  val data = mutable.Map.empty[String, Any]

  def set(key: Key)(value: key.Value): Unit = data.update(key.name, value)

  def get(key: Key): Option[key.Value] =
    data.get(key.name).map(_.asInstanceOf[key.Value])
}

object Database {

  abstract class Key(val name: String) {
    type Value
  }

}

object E extends App {
  trait IntStream[F[_]] {
    def intStream: fs2.Stream[F, Int]
  }
  case class StreamApp[F[_]](
      pipe: fs2.Pipe[F, String, Int]
  ) extends IntStream[F] {

    private def source: fs2.Stream[F, String] = fs2.Stream("1", "2")
    def intStream: fs2.Stream[F, Int] = source.through[F, Int](pipe)
  }
  fs2.Stream.eval(Some(1))
//  val s = StreamApp[Option](???).intStream.compile.drain
}

object MyDatabaseExample extends App {
abstract class Key(val name: String) {
  type ValueType
}

trait Operations {
  def set(k: Key)(v: k.ValueType)(implicit enc: Encoder[k.ValueType]): Unit
  def get(k: Key)(implicit decoder: Decoder[k.ValueType]): Option[k.ValueType]
}

case class Database() extends Operations {
  val db = mutable.Map.empty[String, Array[Byte]]

  def set(k: Key)(v: k.ValueType)(implicit enc: Encoder[k.ValueType]): Unit =
    db.update(k.name, enc.encode(v))

  def get(
      k: Key
  )(implicit decoder: Decoder[k.ValueType]): Option[k.ValueType] =
    db.get(k.name).map(x => decoder.encode(x))
}

trait Encoder[T] {
  def encode(t: T): Array[Byte]
}

object Encoder {
  implicit val stringEncoder: Encoder[String] = (t: String) => t.getBytes
}

trait Decoder[T] {
  def encode(d: Array[Byte]): T
}

object Decoder {
  implicit val stringDecoder: Decoder[String] = (d: Array[Byte]) =>
    new String(d)
}

val db = Database()

def key[Data](v: String) =
  new Key(v) {
    override type ValueType = Data
  }

val k = key[String]("key")
db.set(k)("Hello")
println(db.get(key[String]("key")))
}
