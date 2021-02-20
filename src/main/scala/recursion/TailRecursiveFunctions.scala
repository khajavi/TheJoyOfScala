package recursion

import cats.Semigroup
import cats.kernel.Monoid

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

object TreeRecursion2Example extends App {

  import cats.implicits._

  val _3 = Semigroup[Int].combine(1, 2)
  assert(_3 == 3)
  val _64 = Semigroup[Int => Int].combine(_ + 1, _ * 10).apply(6)
  assert(_64 == 67)
  val _4 = Semigroup[Int].combineN(1, 4)
  assert(_4 == 4)

  val combine = Monoid[String].combineAll(List("a", "b", "c"))
  val M       = implicitly[Monoid[String]]
  val empty   = Monoid[String].empty
  val x       = "A"
  assert(M.combine(x, empty) == M.combine(empty, x))
  assert(M.combine(x, empty) == x)
  assert(M.combineAll(Seq("A", "B")) == "AB")
  
  val l = List(1,2,3,4,5)
  l.foldMap(identity)
  

}

object ListFoldExample extends App {
  
  val list = List(1,2,3,4,5)
  
  def foldList[T, S](list: List[T], seed: S)(f: (S, T) => S): S = {
    list match {
      case ::(head, next) => foldList(next, f(seed, head))(f)
      case Nil => seed
    } 
  }

  println(foldList(list, 0)((x, y) => x + y))

}