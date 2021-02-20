package fs2stream

import fs2.Fallible.raiseThrowableInstance

object FromEitherExample extends App {

  val left = Left(new Exception)

  fs2.Stream.fromEither(left).compile.drain

  ???

}
