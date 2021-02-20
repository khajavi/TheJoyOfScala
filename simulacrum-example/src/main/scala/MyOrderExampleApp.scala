object MyOrderExampleApp extends App {

  import TypeClasses.{MyOrder, Semigroup}
  import TypeClasses.MyOrderImplicits.intInstance
  import MyOrder.AllOps
  import MyOrder.ops._

  println(1 ?!? 2)
}
