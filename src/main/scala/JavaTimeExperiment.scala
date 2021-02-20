import java.time.{
  LocalDateTime,
  LocalTime,
  OffsetDateTime,
  ZoneId,
  ZoneOffset,
  ZonedDateTime
}
import java.util.{GregorianCalendar, Locale}

import com.sun.org.apache.xerces.internal.util.DatatypeMessageFormatter
import javax.xml.datatype.{DatatypeConstants, XMLGregorianCalendar}
import javax.xml.namespace.QName

/**
  * @author Milad Khajavi <khajavi@gmail.com>.
  */
object JavaTimeExperiment extends App {
  def gregorianCalendar(d: ZonedDateTime): XMLGregorianCalendar = {
//        val mask = (if (d.getYear != -2147483648) 32
//        else 0) | (if (this.month != -2147483648) 16
//        else 0) | (if (this.day != -2147483648) 8
//        else 0) | (if (this.hour != -2147483648) 4
//        else 0) | (if (this.minute != -2147483648) 2
//        else 0) | (if (this.second != -2147483648) 1
//        else 0)   val factory = javax.xml.datatype.DatatypeFactory.newInstance()
    factory.newXMLGregorianCalendar(
      d.getYear,
      d.getMonthValue,
      d.getDayOfMonth,
      d.getHour,
      d.getMinute,
      d.getSecond,
      d.getNano / 1000000,
      d.getOffset.getTotalSeconds / 60
    )
  }

  val factory = javax.xml.datatype.DatatypeFactory.newInstance()
  val t: XMLGregorianCalendar =
    factory.newXMLGregorianCalendar("2002-10-09T11:00:00+05:01")

  val t2: XMLGregorianCalendar = {
    factory.newXMLGregorianCalendar("01:03:04")
  }

  val t3 = t2.toGregorianCalendar.toZonedDateTime
  println(gregorianCalendar(t3))

//  override def getXMLSchemaType = {
//    val mask = (if (this.year != -2147483648) 32
//    else 0) | (if (this.month != -2147483648) 16
//    else 0) | (if (this.day != -2147483648) 8
//    else 0) | (if (this.hour != -2147483648) 4
//    else 0) | (if (this.minute != -2147483648) 2
//    else 0) | (if (this.second != -2147483648) 1
//    else 0)
//    mask match {
//      case 7 =>
//        DatatypeConstants.TIME
//      case 8 =>
//        DatatypeConstants.GDAY
//      case 16 =>
//        DatatypeConstants.GMONTH
//      case 24 =>
//        DatatypeConstants.GMONTHDAY
//      case 32 =>
//        DatatypeConstants.GYEAR
//      case 48 =>
//        DatatypeConstants.GYEARMONTH
//      case 56 =>
//        DatatypeConstants.DATE
//      case 63 =>
//        DatatypeConstants.DATETIME
//      case _ =>
//        throw new IllegalStateException(this.getClass.getName + "#getXMLSchemaType() :" + DatatypeMessageFormatter.formatMessage(null.asInstanceOf[Locale], "InvalidXGCFields", null.asInstanceOf[Array[AnyRef]]))
//    }
//  }

}
