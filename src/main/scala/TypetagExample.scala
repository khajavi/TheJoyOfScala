import scala.reflect.ClassTag

object TypetagExample extends App {

  import scala.reflect.runtime.universe._

  val tt = implicitly[TypeTag[List[Int]]]

  typeOf[List[Int]] match {
    case t if t =:= typeOf[List[Int]] => println("list int")
  }


  def f[T](implicit tt: TypeTag[T]) = typeOf[T] match {
    case t if t =:= typeOf[List[Int]] => println("list of Int")
  }

  f[List[Int]]

  def ff[T: TypeTag] = typeOf[T] match {
    case t if t =:= typeOf[List[Int]] => println("list of Int")
  }

  ff[List[Int]]


  //  def fff[T: ClassTag](something: ) = classOf[T] match {
  //    case t: List => println("list of int")
  //
  //  }

  def extract[T: ClassTag](list: List[Any]) =
    list.flatMap {
      case e: T => Some(e)
      case _ => None
    }

  println(
    extract[List[Int]](List(1, "String1", 1.2, "String2", List(), List(1, 2, 3)))
  )

  def recognize[T: TypeTag](list: T) =
      implicitly[TypeTag[T]].tpe match {
      case TypeRef(utype, usymbol, args) =>
        List(utype, usymbol, args).mkString("\n")
    }


  println(recognize(List(List(List(1)))))


  def foo[T] = weakTypeOf[T]

  println(foo[List[Int]])
}
