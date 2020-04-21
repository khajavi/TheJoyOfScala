package typeclass

/**
 * I've wrote these steps from this article:
 *   https://miklos-martin.github.io/learn/fp/2017/08/31/typeclasses-roll-your-own.html
 */
object TypeclassExample1 extends App {

  case class User(id: Int, name: String)

  trait Database {
    def load(id: Int): User

    def save(user: User): Unit
  }

  def updateUser(userId: Int, newName: String)(db: Database): User = {
    val user    = db.load(userId)
    val updated = user.copy(name = newName)
    db.save(updated)
    updated
  }
}


object TypeclassExample2 extends App {

  case class User(id: Int, name: String)

  trait Database[F[_]] {
    def load(id: Int): F[User]

    def save(user: User): F[Unit]
  }

  type Id[T] = T

  trait ImperativeCombinator[F[_]] {
    def doAndThen[A, B](fa: F[A])(f: A => F[B]): F[B]

    def returns[A](a: A): F[A]
  }

  def updateUser[F[_]](userId: Int, newName: String)(db: Database[F], imp: ImperativeCombinator[F]): F[User] = {
    imp.doAndThen(db.load(userId)) { user =>
      val updated = user.copy(name = newName)
      imp.doAndThen(db.save(updated)) { - =>
        imp.returns(updated)
      }
    }
  }
}


object TypeclassExample3 extends App {

  case class User(id: Int, name: String)

  trait Database[F[_]] {
    def load(id: Int): F[User]

    def save(user: User): F[Unit]
  }

  type Id[T] = T

  trait Monad[F[_]] {
    def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]

    def pure[A](a: A): F[A]
  }

  def updateUser[F[_]](userId: Int, newName: String)(implicit db: Database[F], monad: Monad[F]): F[User] = {
    monad.flatMap(db.load(userId)) { user =>
      val updated = user.copy(name = newName)
      monad.flatMap(db.save(updated)) { - =>
        monad.pure(updated)
      }
    }
  }

  implicit val db   : Database[Id] = ???
  implicit val monad: Monad[Id]    = ???
}


object TypeclassExample4 extends App {

  case class User(id: Int, name: String)

  trait Database[F[_]] {
    def load(id: Int): F[User]

    def save(user: User): F[Unit]
  }

  type Id[T] = T

  trait Monad[F[_]] {
    def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]

    def pure[A](a: A): F[A]
  }

  def updateUser[F[_] : Database[F] : Monad[F]](userId: Int, newName: String): F[User] = {
    val db    = implicitly[Database[F]](???) // we should implement instances
    val monad = implicitly[Monad[F]](???) // we should implement instances
    monad.flatMap(db.load(userId)) { user =>
      val updated = user.copy(name = newName)
      monad.flatMap(db.save(updated)) { - =>
        monad.pure(updated)
      }
    }
  }

}


object TypeclassExample5 extends App {

  case class User(id: Int, name: String)

  trait Database[F[_]] {
    def load(id: Int): F[User]

    def save(user: User): F[Unit]
  }

  object Database {
    def apply[F[_]](): Database[F] = implicitly[Database[F]](???) // we should implement instances
  }

  type Id[T] = T

  trait Monad[F[_]] {
    def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]

    def pure[A](a: A): F[A]
  }

  object Monad {
    def apply[F[_]](): Monad[F] = implicitly[Monad[F]](???) // we should implement instances
  }

  def updateUser[F[_] : Database[F] : Monad[F]](userId: Int, newName: String): F[User] = {
    val db    = Database[F]
    val monad = Monad[F]
    monad.flatMap(db.load(userId)) { user =>
      val updated = user.copy(name = newName)
      monad.flatMap(db.save(updated)) { - =>
        monad.pure(updated)
      }
    }
  }

}


object TypeclassExample6 extends App {

  case class User(id: Int, name: String)

  trait Database[F[_]] {
    def load(id: Int): F[User]

    def save(user: User): F[Unit]
  }

  object Database {

    object syntax {
      def save[F[_]](user: User)(implicit db: Database[F]): F[Unit] = db.save(user)

      def load[F[_]](id: Int)(implicit db: Database[F]): F[User] = db.load(id)
    }

  }

  type Id[T] = T

  trait Monad[F[_]] {
    def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]

    def pure[A](a: A): F[A]
  }

  object Monad {

    object syntax {
      def flatMap[F[_], A, B](fa: F[A])(f: A => F[B])(implicit m: Monad[F]): F[B] =
        m.flatMap(fa)(f)

      def pure[F[_], A](a: A)(implicit m: Monad[F]) = m.pure(a)
    }

  }

  import Database.syntax._
  import Monad.syntax._

  def updateUser[F[_] : Database[F] : Monad[F]](userId: Int, newName: String): F[User] = {
    implicit val database = implicitly[Database[F]](???)
    implicit val monad    = implicitly[Monad[F]](???)

    flatMap(load(userId)) { user =>
      val updated = user.copy(name = newName)
      flatMap(save(updated)) { - =>
        pure(updated)
      }
    }
  }

}


object TypeclassExample7 extends App {

  case class User(id: Int, name: String)

  trait Database[F[_]] {
    def load(id: Int): F[User]

    def save(user: User): F[Unit]
  }

  object Database {

    object syntax {
      def save[F[_]](user: User)(implicit db: Database[F]): F[Unit] = db.save(user)

      def load[F[_]](id: Int)(implicit db: Database[F]): F[User] = db.load(id)
    }

  }

  type Id[T] = T

  trait Monad[F[_]] {
    def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]

    def map[A, B](fa: F[A])(f: A => B): F[B]

    def pure[A](a: A): F[A]
  }

  object Monad {

    object syntax {
      def flatMap[F[_], A, B](fa: F[A])(f: A => F[B])(implicit m: Monad[F]): F[B] =
        m.flatMap(fa)(f)

      def map[F[_], A, B](fa: F[A])(f: A => B)(implicit m: Monad[F]): F[B] = m.map(fa)(f)

      def pure[F[_], A](a: A)(implicit m: Monad[F]) = m.pure(a)
    }

  }

  import Database.syntax._
  import Monad.syntax._

  def updateUser[F[_] : Database[F] : Monad[F]](userId: Int, newName: String): F[User] = {
    implicit val database = implicitly[Database[F]](???)
    implicit val monad    = implicitly[Monad[F]](???)

    flatMap(load(userId)) { user =>
      val updated = user.copy(name = newName)
      map(save(updated))(_ => updated)
    }
  }

}


object TypeclassExample8 extends App {

  case class User(id: Int, name: String)

  trait Database[F[_]] {
    def load(id: Int): F[User]

    def save(user: User): F[Unit]
  }

  object Database {

    object syntax {

      //      implicit class DatabaseOps[F[_], A](fa: F[A])(implicit db: Database[F]) {
      def save[F[_]](user: User)(implicit db: Database[F]): F[Unit] = db.save(user)

      def load[F[_]](id: Int)(implicit db: Database[F]): F[User] = db.load(id)
    }

    //    }

  }

  type Id[T] = T

  trait Monad[F[_]] {
    def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]

    def map[A, B](fa: F[A])(f: A => B): F[B]

    def pure[A](a: A): F[A]
  }

  object Monad {

    object syntax {

      implicit class MonadOps[F[_], A](fa: F[A])(implicit m: Monad[F]) {
        def flatMap[B](f: A => F[B])(implicit m: Monad[F]): F[B] =
          m.flatMap(fa)(f)

        def map[B](f: A => B)(implicit m: Monad[F]): F[B] = m.map(fa)(f)

        def pure[B](a: A)(implicit m: Monad[F]) = m.pure(a)
      }

    }

  }

  import Database.syntax._
  import Monad.syntax._

  def updateUser[F[_] : Database[F] : Monad[F]](userId: Int, newName: String): F[User] = {
    implicit val database = implicitly[Database[F]](???)
    implicit val monad    = implicitly[Monad[F]](???)

    for {
      user <- load(userId)
      _ <- save(user)
    } yield user
  }

}

object TypeclassExample9 extends App {

  import cats.{Id, Monad}
  import cats.syntax.flatMap._
  import cats.syntax.functor._

  case class User(id: Int, name: String)

  trait Database[F[_]] {
    def load(id: Int): F[User]

    def save(user: User): F[Unit]
  }

  object Database {

    object syntax {
      def save[F[_]](user: User)(implicit db: Database[F]): F[Unit] = db.save(user)

      def load[F[_]](id: Int)(implicit db: Database[F]): F[User] = db.load(id)
    }

    // We can provide some instances in the companion object, if we like
    implicit val dbId = new Database[Id] {
      def load(id: Int): User = User(id, "some name")

      def save(user: User): Unit = ()
    }
  }

  import Database.syntax._

  def updateUser[F[_] : Database : Monad](userId: Int, newName: String): F[User] = {
    for {
      user <- load(userId)
      updated = user.copy(name = newName)
      _ <- save(user.copy(name = newName))
    } yield updated
  }

  assert(updateUser[Id](1, "some other name") == User(1, "some other name"))
}
