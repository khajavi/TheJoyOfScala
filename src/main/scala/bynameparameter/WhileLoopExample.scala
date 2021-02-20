package bynameparameter

import scala.annotation.tailrec

object WhileLoopExample extends App {
  @tailrec
  def whileLoop[T](cond: => Boolean)(body: => T): T = {
    if (cond) body else whileLoop(cond)(body)
  }
  
  var counter = 0
  whileLoop( counter <= 10)(println("Hello, World!"))
}
