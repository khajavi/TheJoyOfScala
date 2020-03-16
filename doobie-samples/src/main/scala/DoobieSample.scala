import cats.effect._
import cats.implicits._
import doobie._
import doobie.h2.H2Transactor
import doobie.implicits._

object DoobieSample extends IOApp {
  val program1 = 42.pure[ConnectionIO]

  import doobie.util.ExecutionContexts

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContexts.synchronous)

  val transactor: Resource[IO, H2Transactor[IO]] =
    for {
      ce <- ExecutionContexts.fixedThreadPool[IO](32) // our connect EC
      be <- Blocker[IO] // our blocking EC
      xa <- H2Transactor.newH2Transactor[IO](
        "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", // connect URL
        "sa", // username
        "", // password
        ce, // await connection here
        be // execute JDBC operations here
      )
    } yield xa

  override def run(args: List[String]): IO[ExitCode] = {
    transactor.use { xa =>
      val res = for {
        n <- sql"select 42".query[Int].unique
        b <- sql"select random()".query[Double].unique
      } yield (n, b)

      val program = {
        val n = sql"select 42".query[Int].unique
        val b = sql"select random()".query[Double].unique
        (n, b).tupled
      }

      // Construct and run your server here!
      for {
        r <- res.transact(xa)
        _ <- IO(println(r))
      } yield ExitCode.Success
    }

  }
}

