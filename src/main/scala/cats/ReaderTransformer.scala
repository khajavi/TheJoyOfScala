package cats

import cats.data._
import cats.effect.IO

object ReaderTransformer extends App {
  type Config = String
  type Result = String

  def getConfig: IO[Config] = ???

  def serviceCall(c: Config): IO[Result] = ???

  def getConfig1: ReaderT[IO, Config, Config] = ???

  def serviceCall2: ReaderT[IO, Config, Result] = ???

  val res: ReaderT[IO, Config, Result] = serviceCall2.compose(getConfig1)

  val res2: ReaderT[IO, Config, Result] = for {
    config <- ReaderT.ask[IO, Config]
    result <- ReaderT.liftF(serviceCall(config))
  } yield result

  res2.run("config")


  val a1: ReaderT[IO, Config, Config] = ReaderT.ask[IO, Config]
  val a2                              = serviceCall2.compose(a1)
  a2.run("config")

}

object StateTransformerExample extends App {
  type Env = String
  type Request = String
  type Response = String

  def initialEnv: Env = ???

  def request(r: Request, env: Env): IO[Response] = ???

  def updateEnv(r: Response, env: Env): Env = ???

  val req1: Request = ???
  val req2: Request = ???
  val req3: Request = ???
  val req4: Request = ???

  def requestWithState(r: Request): StateT[IO, Env, Response] = for {
    env <- StateT.get[IO, Env]
    resp <- StateT.liftF(request(r, env))
    _ <- StateT.modify[IO, Env](updateEnv(resp, _))
  } yield resp

  def stateProgram: StateT[IO, Env, Response] = for {
    resp1 <- requestWithState(req1)
    resp2 <- requestWithState(req2)
    resp3 <- requestWithState(req3)
    resp4 <- requestWithState(req4)
  } yield resp2

  val program: IO[(Env, Response)] = stateProgram.run(initialEnv)
}


object StateMonadExample extends App {

  val step1 = State[Int, String] { num =>
    val ans = num + 1
    (ans, s"The result of step1 is $ans")
  }

  val step2 = State[Int, String] { num =>
    val ans = num * 2
    (ans, s"The result of step2 is $ans")
  }

  val both = for {
    a <- step1
    b <- step2
  } yield (a, b)

  val (state, result) = both.run(5).value
  println(state, result)

}


object WriterMonad extends App {

  import cats.instances.vector._
  import cats.syntax.applicative._
  import cats.syntax.writer._ // for writer

  type Logged[A] = Writer[Vector[String], A]

  def factorial(n: Int): Logged[Int] = for {
    ans <- if (n == 0) 1.pure[Logged] else factorial(n - 1).map(_ * n)
    _ <- Vector(s"fact $n $ans").tell
  } yield (ans)

  val (log, result) = factorial(5).run
  log.foreach(println)
  println(s"result: $result")
}

object ReaderWriterStateMonadExample extends App {
  import cats.instances.list._

  type User = String
  type UUID = String
  type Id[F] = F

  trait UserRepository[F[_]] {
    def get(uuid: UUID): F[User]
  }

  class IdRepo extends UserRepository[Id] {
    val users = Map(
      "1" -> "Milad",
      "2" -> "Salman",
      "3" -> "Ali"
    )
    override def get(uuid: UUID): Id[User] = users(uuid)
  }

  def getUser(uuid: UUID): ReaderWriterState[IdRepo, List[String], Map[UUID, User], User] =
    ReaderWriterState[IdRepo, List[String], Map[UUID, User], User] { (env, state) =>
      val user = env.get(uuid)
      (List(s"user $uuid cached"), state + (uuid -> user), user)
    }

  val result = for {
    u1 <- getUser("1")
    u2 <- getUser("2")
  } yield (u1, u2)

  val s = result.run(new IdRepo, Map.empty).value

  println(s)
}
