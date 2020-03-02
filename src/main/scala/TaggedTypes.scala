import java.time.LocalDate

import shapeless.tag.{@@, Tagger}
import shapeless.tag
import tags.{BookingId, PaymentId}

trait BookingIdTag

trait PaymentIdTag

package object tags {
  type BookingId = String @@ BookingIdTag
  type PaymentId = String @@ PaymentIdTag
}

case class Booking(id: BookingId, date: LocalDate)

case class Payment(id: PaymentId, bookingId: BookingId, date: LocalDate)

object TaggedTypes {
  val bookingId1: BookingId = tag[BookingIdTag][String]("bookingId1")
  val bookingId2: BookingId = tag[BookingIdTag][String]("bookingId2")


  val paymentId: PaymentId = tag[PaymentIdTag][String]("paymentId")
  val paymentId1: PaymentId = tag[PaymentIdTag][String]("paymentId1")
  val paymentId2: PaymentId = tag[PaymentIdTag][String]("paymentId2")


  val booking1 = Booking(bookingId1, LocalDate.now)
  val booking2 = Booking(bookingId1, LocalDate.now)

  val payment1 = Payment(paymentId1, booking1.id, LocalDate.now)
  val payment2 = Payment(paymentId2, booking2.id, LocalDate.now)

  val bookingPaymentMapping: Map[BookingId, PaymentId] = Map(booking1.id -> payment1.id, booking2.id -> payment2.id)
}

object Payments {

  import TaggedTypes._

  def payBooking(bookingId: BookingId) = Payment(paymentId, bookingId, LocalDate.now)
}



object TaggedTypesSample2 extends App {
  import shapeless.tag
  import shapeless.tag.@@
  trait NameTag
  trait AgeTag
  type Name = String @@ NameTag
  type Age = Int @@ AgeTag
  case class Person(name: Name, age: Age)
  println(Person(tag[NameTag][String]("Milad"), tag[AgeTag][Int](30)))
  println(Person(new Tagger[NameTag].apply[String]("Milad"), tag[AgeTag][Int](30)))
}
