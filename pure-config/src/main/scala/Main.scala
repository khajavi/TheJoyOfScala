import pureconfig.ConfigReader.Result

object Main extends App {

  import pureconfig._
  import pureconfig.generic.auto._

  case class Port(number: Int) extends AnyVal

  sealed trait AuthMethod

  case class Login(username: String, password: String) extends AuthMethod

  case class Token(token: String) extends AuthMethod

  case class PrivateKey(pkFile: java.io.File) extends AuthMethod

  case class ServiceConf(
                          host: String,
                          port: Port,
                          useHttps: Boolean,
                          authMethods: List[AuthMethod]
                        )

  val config: Result[ServiceConf] = ConfigSource.default.load[ServiceConf]
  config.foreach(println)
}
