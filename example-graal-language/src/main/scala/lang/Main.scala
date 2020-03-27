package lang

import com.oracle.truffle.api.TruffleLanguage.{Env, ParsingRequest}
import com.oracle.truffle.api.frame._
import com.oracle.truffle.api.nodes.Node.Child
import com.oracle.truffle.api.nodes._
import com.oracle.truffle.api.{Option => _, _}
import org.graalvm.polyglot.{Context => Ctx}

import scala.annotation.meta.field

trait ExampleContext

class ExampleLang extends TruffleLanguage[ExampleContext] {
  def createContext(env: Env): ExampleContext = {
    ExampleLang.INSTANCE = this;
    new ExampleContext {}
  }

  def isObjectOfLanguage(obj: Any): Boolean = false

  // Here we can implement a parser, later
  override def parse(req: ParsingRequest): CallTarget = ???
}

object ExampleLang {
  private[lang] var INSTANCE: ExampleLang = null

  def instance = INSTANCE

  def apply[T](f: ExampleLang => T): T = {
    val ctx = Ctx.create()
    ctx.initialize("example")
    ctx.enter()
    val res = try {
      f(ExampleLang.INSTANCE)
    } finally {
      ctx.leave()
      ctx.close()
    }
    res
  }

  // https://www.scala-lang.org/api/current/scala/annotation/meta/index.html
  type child = Child@field
}

import lang.ExampleLang.child

abstract class Exp[@specialized +T] extends Node {
  def apply(frame: VirtualFrame): T
}

object One extends Exp[Int] {
  def apply(frame: VirtualFrame): Int = 1
}

// a node that adds the two children
final case class Add(@child var lhs: Exp[Int], @child var rhs: Exp[Int]) extends Exp[Int] {
  final def apply(frame: VirtualFrame): Int =
    lhs(frame) + rhs(frame)
}

final class SomeFun(language: ExampleLang) extends RootNode(language) {
  override def execute(frame: VirtualFrame): Integer = {
    Add(Add(One, One), One)(frame)
  }
}


object Main extends App {
  val a = ExampleLang[Unit] { lang =>
    val runtime: TruffleRuntime = Truffle.getRuntime
    val rootNode                = new SomeFun(lang)
    val target                  = runtime.createCallTarget(rootNode)
    println(target.call())
  }
}
