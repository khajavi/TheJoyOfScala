package tsec

import tsec.hashing.jca.SHA256
import zio.{ Task, ZIO}
import tsec.common._

object SecuritySample extends App {
  def hash(data: String): Array[Byte] = for {
    hash <- SHA256.hash(data.utf8Bytes)
  } yield hash

  println(hash("hello"))
}
