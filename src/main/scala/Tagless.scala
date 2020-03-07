import cats.Monad

object Tagless extends App {

  def putStrLn(line: String): IO[Unit] =
    IO.effect(println(line))

  putStrLn("heeloo").unsafeInterpret()

  def innocent[F[_]: Monad]: F[Unit] = {
    println("What guarantees?")

    Monad[F].point(())
  }
}




class IO[+A](val unsafeInterpret: () => A) { s =>
  def map[B](f: A => B) = flatMap(f.andThen(IO.effect(_)))
  def flatMap[B](f: A => IO[B]): IO[B] =
    IO.effect(f(s.unsafeInterpret()).unsafeInterpret())
}
object IO {
  def effect[A](eff: => A) = new IO(() => eff)
}

