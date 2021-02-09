package generics

// Borrow example from https://stackoverflow.com/a/48858344/225052
object CovarianceExample extends App {

  sealed trait Entertainment

  case class ReadingBook() extends Entertainment

  case class WatchingFilm() extends Entertainment

  trait Producer[+T] {
    def produce: T
  }

  // Whenever we need Entertainment we can substitute it with ReadingBook or WatchingFilm 
  // ReadingBook <: Entertainment
  // WatchingFilm <: Entertainment
  // Whenever we need Producer[Entertainment] we can substitute it with Producer[ReadingBook] or Producer[WatchingFilm]
  // Producer[ReadingBook] <: Producer[Entertainment]
  // Producer[WatchingFilm] <: Producer[Entertainment]

  val generalPerson = new Producer[Entertainment] {
    override def produce: Entertainment = {
      val morning = true
      if (morning) ReadingBook() else WatchingFilm()
    }
  }

  val bookWorm = new Producer[ReadingBook] {
    override def produce: ReadingBook = ReadingBook()
  }

  val entertainment1: Entertainment = generalPerson.produce
  val entertainment2: Entertainment = bookWorm.produce

  //can't compile 
  //  val readingBook: ReadingBook = generalPerson.produce 

}
