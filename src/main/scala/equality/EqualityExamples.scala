package equality

object EqualityExamples extends App {
  val nullString: String = null
  val nullInteger: String = null
  assert(nullString == nullInteger)

  val a = new String("Foo")  //a is an instance of AnyRef on the heap
  val b = new String("Foo")  //b is an instance of AnyRef on the heap
  val c = a
  
  assert(a == b)
  assert(a eq c)
  assert(a ne b)

  assert(a equals b)
  assert(a.hashCode == b.hashCode)
  
  case class Event(a: Int)
  val fooEvent = Event(1)
  val barEvent = Event(1)
  assert(fooEvent.hashCode() == barEvent.hashCode())
  assert(fooEvent equals barEvent)
  assert(fooEvent ne barEvent)
}
