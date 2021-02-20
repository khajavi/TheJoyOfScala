/**
 * @author Milad Khajavi <khajavi@gmail.com>.
 */
package example

import java.util._


import scala.util.Random
import scala.concurrent._
import scala.concurrent.duration._

object DataTransform  extends App {

  ////// DO NOT MODIFY THESE ///////
  final case class User(id: UUID)

  def listUsers(implicit ec: ExecutionContext): LazyList[User] = LazyList.continually(User(UUID.randomUUID()))

  def scoreUser(user: User)(implicit ec: ExecutionContext): Future[Long] = Future {
    // some slow db query
    Thread.sleep(1000+Random.nextInt(5000))
    user.id.hashCode()
  }

  ////// START HERE ///////

  // 1) list 5 users using method listUsers and print sum of their scores
  implicit val ec = ExecutionContext.global
  listUsers.take(10).foreach(println)


  // 2) create 10 groups of users (of size 3), where each group will contain sum of scores of members, and print it
  def take3 = listUsers.take(3)
  (1 to 10).map(_ => take3)



//  for {
//  groups <- Future((1 to 10).map(_ => take3))
//  sum <- Future.sequence(group.map(scoreUser)).map(_.sum)
//  } yield (sum)

  // 3) same as 2), but 100 groups of size 10, and print it
}