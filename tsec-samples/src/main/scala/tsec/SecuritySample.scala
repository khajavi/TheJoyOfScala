package tsec

import tsec.common._
import tsec.hashing.jca.SHA256

object SecuritySample extends App {
  def hash(data: String): Array[Byte] = for {
    hash <- SHA256.hash(data.utf8Bytes)
  } yield hash

  println(hash("hello"))
}
