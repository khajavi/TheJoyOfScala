package recursion

import scala.annotation.tailrec


object InsertionSortExample1 extends App {
  def insertionSort(list: List[Int]): List[Int] = {
    list match {
      case ::(head, next) =>
        val (left, right) = next.partition(_ < head)
        insertionSort(left) ::: head :: insertionSort(right)
      case Nil => Nil
    }
  }

  assert(insertionSort(List(2, 3, 1)) == List(1, 2, 3))
}

//object InsertionSortExample2 extends App {
//  def insertionSort(list: List[Int], acc: List[Int] = Nil): List[Int] = {
//    @tailrec
//    def insert(x: Int, sorted: List[Int], acc: List[Int]): List[Int] = {
//      sorted match {
//        case ::(head, next) if (x < head) => acc ::: x :: head :: next
//        case Nil =>
//      } 
//    }
//    
//    acc match {
//      case ::(head, next) =>
//       insertionSort(next, insert(head, acc, Nil))
//      case Nil => acc
//    }
//  }
//
//  println(insertionSort(List(3, 2, 5)))
//  //  assert(insertionSort(List(2,1,6,3)) == List(1,2,3,6))
//}

