package typeinference

import typeinference.InferenceExample.{f1, f2}

object InferenceExample extends App {

  class Foo {
    class Bar
  }

  val f1 = new Foo
  val b1: f1.Bar = new f1.Bar

  val f2 = new Foo
  val b2: f2.Bar = new f2.Bar
}
