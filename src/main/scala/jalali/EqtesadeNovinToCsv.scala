package jalali

import com.github.tototoshi.csv.{CSVReader, CSVWriter}

object EqtesadeNovinToCsv extends App {
  def jalali2gregorian(jalali: String): String = {
    import scala.language.postfixOps
    import sys.process._
//    val bin       = "/home/milad/ssd/.linuxbrew/bin/jalalim"
    val bin = "/home/milad/.linuxbrew/bin/jalalim"
    val gregorian = s"$bin togregorian $jalali" !!

    gregorian.trim
  }


  def csvToStream(file: String) = {
    val reader = CSVReader.open(file)
    reader.all().map(_.toArray)
  }


  println(jalali2gregorian("1394/03/10"))

  val url     = "/home/milad/Dropbox/Documents/enovin.csv"
  val refined = csvToStream(url).map { record =>
    List(
      record(0) ,
      jalali2gregorian(record(1)),
      if (refine(record(3).trim) != "")
        refine(record(3).trim) + ".0"
      else
        "-" + refine(record(4).trim) + ".0",
      record(1) , record(2) ,record(5), record(9)
    )
  }

  val writer = CSVWriter.open(s"$url.refined")
  writer.writeAll(refined)


  def refine(str: String): String = {
    val formatter = java.text.NumberFormat.getIntegerInstance
    val amount = formatter.format(str.toLong)
    if (amount == "0")
      amount.replace("0", "")
    else amount
  }

}


