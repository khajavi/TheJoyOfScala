import scalatags.Text
import scalatags.Text.all._

object Main extends App {
  val m: Text.TypedTag[String] = html(
    head(
      script("some script")
    ),
    body(
      h1("This is my title"),
      div(
        p("This is my first paragraph"),
        p("This is my second paragraph")
      )
    )
  )
  println(m.toString())
}
