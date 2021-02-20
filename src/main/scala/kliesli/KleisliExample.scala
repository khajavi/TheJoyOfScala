package kliesli

import cats.data.{Kleisli, State}

object KleisliExample extends App {
  val x = Kleisli[Option, Int, String](x => Some(x.toString))
  
  println(x.run(1))
  
}
