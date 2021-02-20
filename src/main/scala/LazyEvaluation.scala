object LazyEvaluation extends App {
  println(xyz.syncEagerWork)

  println(xyz.asyncEagerWork(() => println("hello")))
}


trait Work[A] {
  def syncEagerWork: A

  def syncLazyWork: () => A

  def asyncEagerWork(f: () => Unit): Unit

  def asyncLazyWork: () => (() => Unit) => Unit

}


object xyz extends Work[Int] {
  override def syncEagerWork: Int = 4

  override def syncLazyWork: () => Int = () => 4

  override def asyncEagerWork(f: () => Unit):  Unit = f()

  override def asyncLazyWork: () => (() => Unit) => Unit = () => asyncEagerWork
}


