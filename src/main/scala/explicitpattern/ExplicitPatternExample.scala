package explicitpattern

// I learned this pattern during review process of zio-crypto from John De Goes
object ExplicitPatternExample extends App {

  trait Secure[-Algorithm]

  sealed trait HashAlgorithm

  object HashAlgorithm {
    type SHA256 = SHA256.type
    type SHA512 = SHA512.type
    type MD5 = MD5.type

    case object SHA256 extends HashAlgorithm {
      implicit val secure: Secure[SHA256] = new Secure[SHA256] {}
      implicit val self: SHA256 = SHA256
    }

    case object SHA512 extends HashAlgorithm {
      implicit val secure: Secure[SHA512] = new Secure[SHA512] {}
      implicit val self: SHA512 = SHA512
    }

    case object MD5 extends HashAlgorithm {
      implicit val self: MD5 = MD5
    }

  }

  def hash[Alg <: HashAlgorithm](implicit secure: Secure[Alg], alg: Alg) = {
    alg.asInstanceOf[HashAlgorithm] match {
      case HashAlgorithm.SHA256 => println("sha256")
      case HashAlgorithm.SHA512 => println("sha512")
      case HashAlgorithm.MD5    => println("md5")
    }
  }

  def unsecure[A](f: Secure[Any] => A): A = f(new Secure[Any] {})

  unsecure { implicit s =>
    hash[HashAlgorithm.MD5]
  }

  hash[HashAlgorithm.SHA256]
}
