package recursion

import scala.annotation.tailrec

object TailRecursiveFunctions extends App {
  def nonTailRecursive(l: List[_]): Int =
    l match {
      case ::(_, next) => nonTailRecursive(next) + 1
      case Nil => 0
    }

  println(nonTailRecursive(List.fill(100)(1)))

  def whileLength(l: List[_]): Int = {
    var cursor = l
    var count  = 0

    while (cursor != Nil) {
      count += 1
      cursor = cursor.tail
    }
    count
  }

  println(whileLength(List.fill(100000)(1)))

  def tailRecursiveLength(l: List[_]): Int = {
    @tailrec
    def loop(count: Int, cursor: List[_]): Int = {
      cursor match {
        case ::(_, next) => loop(count + 1, next)
        case Nil => count
      }
    }

    loop(count = 0, cursor = l)
  }

  println(tailRecursiveLength(List.fill(1000000)(1)))


  def foldLength(l: List[_]): Int =
    l.foldLeft(0) { case (count, _) => count + 1 }

  println(foldLength(List.fill(1000000)(1)))


  def manualFoldLength[E, R](l: List[E], seed: R)(f: (R, E) => R): R =
    l match {
      case ::(head, next) => {
        val nextSeed = f(seed, head)
        manualFoldLength(next, nextSeed)(f)
      }
      case Nil => seed
    }

  val length = manualFoldLength(List.fill(1000)(1), 0) {
    case (seed, _) => seed + 1
  }
  println(s"length of list with manual folding: ${length}")

  def concatString(l: List[String]): String =
    l.foldLeft("") { case (buffer, e) => buffer + e }

  println(concatString(List("1", "b", "c", "3")))
}

object TreeRecursionExample extends App {

  sealed trait Tree[+A]

  case class Node[+A](value: A, left: Tree[A], right: Tree[A]) extends Tree[A]

  case object Empty extends Tree[Nothing]

  def nonTailRecFoldTree[A, S](tree: Tree[A], seed: S)(f: (S, A) => S): S = {
    tree match {
      case Empty => seed
      case Node(value, left, right) =>
        val leftR = nonTailRecFoldTree(left, f(seed, value))(f)
        nonTailRecFoldTree(right, leftR)(f)
    }
  }

  val res = nonTailRecFoldTree(Node(1, Node(2, Empty, Empty), Empty), 0) { case (counter, _) => counter + 1 }
  println(res)

  def tailRecFoldTree[A, S](tree: Tree[A], seed: S)(f: (S, A) => S): S = {
    @tailrec
    def loop(tree: List[Tree[A]], state: S): S = {
      tree match {
        case Nil => state
        case ::(head, tail) => head match {
          case Node(value, left, right) => loop(left :: right :: tail, f(state, value))
          case Empty => loop(tail, state)
        }
      }
    }

    loop(List(tree), seed)
  }
}
