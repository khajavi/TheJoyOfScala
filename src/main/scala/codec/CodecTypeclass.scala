package codec

object CodecTypeclass {
  type Codec1[A, B] = Encode[A, B] with Decode[B, A]
  type Codec2[A, B] = (A => B, B => Option[A])
  type Serialize[A] = Encode[A, Array[Byte]]
  type Deserialize[A] = Decode[Array[Byte], A]
}

trait Encode[A, B] {
  def encode(a: A): B
}

trait Decode[A, B] {
  def decode(a: A): Option[B]
}


