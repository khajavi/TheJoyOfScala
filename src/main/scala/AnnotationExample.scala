import scala.beans.BeanProperty

case class Person(@BeanProperty var age : Int)

object AnnotationExample extends App {
  val ali = Person(20)
  ali.setAge(21)
  println(ali)
}
