class B extends FooA {
}

object CompatibilityExample extends App {
  val b = new B
  b.log("hello")
}
