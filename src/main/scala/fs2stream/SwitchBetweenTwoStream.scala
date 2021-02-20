package fs2stream

import cats.effect
import cats.effect.IO.ioConcurrentEffect
import cats.effect._
import fs2._
import fs2.concurrent.{Queue, SignallingRef, Topic}
import fs2stream.info.particleb.exchanges.binance.stream.InMemoryMessageBus

import scala.concurrent.duration.{
  Duration,
  DurationDouble,
  DurationInt,
  FiniteDuration
}

object SwitchBetweenTwoStream extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    val s = SignallingRef[IO, Int](0)
    val s1 = Stream
      .range[IO](1, 100)
      .evalTap(_ => IO.sleep(0.2.seconds))
      .interruptAfter(3.seconds)
      .onFinalize[IO](s.flatMap(_.modify(_ => (1, ()))))
      .debug()
//    val x = s.map(_.continuous.drop(0).)
    val s2 = Stream.range[IO](11, 20).evalTap(_ => IO.sleep(1.seconds))
    val stream = for {
      r <- s.map(_.discrete.filter(_ > 0).debug().switchMap(x => s2))
    } yield (r) concurrently s1
    stream
      .flatMap(
        _.evalTap(x => IO(println(x))).compile.drain.as(ExitCode.Success)
      )
      .as(ExitCode.Success)
  }
}

object SwitchBetweenTwoStream2 extends IOApp {
  def source = {
    Stream
      .awakeEvery[IO](1.seconds)
      .map(_.toSeconds)
      .interruptAfter(10.seconds)
      .debug(logger = x => println(s"src: $x"))
      .onFinalize(IO(println("closed the source stream")))
  }
  override def run(args: List[String]): IO[ExitCode] = {
    val stream = for {
//      q1 <- Stream.eval(Queue.unbounded[IO, Long])
//      q2 <- Stream.eval(Queue.unbounded[IO, Long])
//      src = source.evalTap(x => q1.enqueue1(x) *> q2.enqueue1(x))
//      s1 = q1.dequeue
//      s2 = q2.dequeue
      s <- switchTwoStream(8.seconds, 1.seconds)(source)
    } yield (s)
    stream
      .debug(logger = x => println(s"rr: $x"))
      .compile
      .drain
      .as(ExitCode.Success)
  }

  def switchTwoStream(
      switchAfter: FiniteDuration,
      beforehandCandidateStartTime: FiniteDuration
  )(
      decaying: Stream[IO, Long]
//      candidate: Stream[IO, Long]
  ): Stream[IO, Long] = {
    val candidate = decaying
    val newStream = for {
      earliest <- Stream.eval(SignallingRef[IO, Boolean](false))
      dq1 <- Stream.eval(Queue.unbounded[IO, Long])
      dq2 <- Stream.eval(Queue.unbounded[IO, Long])
      cq <- Stream.eval(Queue.unbounded[IO, Long])
      res <- (decaying
          .evalTap(x => dq1.enqueue1(x) *> dq2.enqueue1(x))
          .interruptWhen(
            earliest.discrete
              .delayBy(switchAfter)
              .debug() concurrently dq1.dequeue concurrently dq2.dequeue.head
              .onFinalize(
                earliest.update(_ => true)
              )
          )
          .debug(logger = x => println(s"decaying: $x"))
          .onFinalize(
            IO(println("decaying stream closed."))
          )
          .debug(logger = s => println(s"ds: $s")) switchMap (i =>
          Stream.emit(i) ++ cq.dequeue concurrently candidate
            .delayBy(switchAfter - beforehandCandidateStartTime)
            .debug(logger = x => println(s"candidate started: $x"))
            .through(cq.enqueue)
        ))
    } yield (res)
    newStream
//    newStream ++ switchTwoStream(switchAfter, beforehandCandidateStartTime)(
//      candidate
//    )
  }
}

object InterruptExample extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    val s = Stream.range[IO](1, 100).metered(1.seconds)
    val interrupt = (Stream(false).repeatN(5) ++ Stream.emit(false))
      .covary[IO]
      .metered(1.seconds)
      .debug(logger = x => println(s"i: $x"))
    s.interruptWhen(interrupt).debug().compile.drain.as(ExitCode.Success)
  }
}

object InfiniteSwitch extends IOApp {
  def switch(a: Stream[IO, Int]): Stream[IO, Int] = {
    def newStream: Stream[IO, Int] = Stream[IO, Int](1, 2, 3).metered(1.seconds)
    a ++ switch(newStream)
  }

  override def run(args: List[String]): IO[ExitCode] = {
    val stream = for {
      r <- switch(Stream.range[IO](1, 10))
    } yield (r)
    stream.debug().compile.drain.as(ExitCode.Success)
  }
}

object InfiniteSwitch2 extends IOApp {
  def stream =
    Stream
      .awakeEvery[IO](1.seconds)
      .interruptAfter(5.seconds)
      .map(_.toSeconds.toInt)
  def switch(a: Stream[IO, Int]): Stream[IO, Int] = {
    def newStream: Stream[IO, Int] = Stream[IO, Int](1, 2, 3).metered(1.seconds)
    a ++ switch(newStream)
  }
  override def run(args: List[String]): IO[ExitCode] = {
    val res: Stream[IO, Int] = for {
      r <- switch(stream)
    } yield (r)
    res.debug().compile.drain.as(ExitCode.Success)
  }
}

object InfiniteSwitch3 extends IOApp {
  def source =
    Stream
      .awakeEvery[IO](1.seconds)
      .interruptAfter(5.seconds)
      .map(_.toSeconds.toInt)
      .onFinalize(IO(println("finalized")))
  def switch(a: Stream[IO, Int]): Stream[IO, Int] = {
    a ++ switch(source)
  }
  override def run(args: List[String]): IO[ExitCode] = {
    val res: Stream[IO, Int] = for {
      r <- switch(source)
    } yield (r)
    res.debug().compile.drain.as(ExitCode.Success)
  }
}

object InfiniteSwitch4 extends IOApp {
  val s1: Stream[IO, Int] = Stream
    .range[IO](1, 100)
    .metered(1.seconds)
    .interruptAfter(5.seconds)
    .onFinalize(IO(println("finalized")))
  override def run(args: List[String]): IO[ExitCode] = {
    def label: String => String => Unit = { x => { y => println(s"$x: $y") } }
    def switch(
        s: Stream[IO, Int]
//        after: FiniteDuration,
//        beforehand: FiniteDuration
    ): Stream[IO, Int] = {
      for {
        q <- Stream.eval(Queue.unbounded[IO, Int])
        i <- s.switchMap(x =>
          Stream.emit(x).debug(logger = label("R")) ++ s.debug(logger =
            label("T")
          )
        ) ++ switch(s).debug(logger = label("S"))
      } yield i
    }
    val res = switch(s1)

    res.debug().compile.drain.as(ExitCode.Success)
  }
}

object InfiniteSwitch5 extends IOApp {
  def s1: Stream[IO, Int] =
    Stream
      .range[IO](1, 100)
      .metered(1.seconds)
      .interruptAfter(5.seconds)
      .onFinalize(IO(println("finalized")))
  override def run(args: List[String]): IO[ExitCode] = {
    def label: String => String => Unit = { x => { y => println(s"$x: $y") } }
    def switch(s: Stream[IO, Int], l: String): Stream[IO, Int] = {
      s.debug(logger = label(l))
        .onFinalize(IO(println("terminated"))) ++ switch(s, l + l)
    }
    val res = switch(s1, "0")
    res.debug().compile.drain.as(ExitCode.Success)
  }
}

object InfiniteSwitch6 extends IOApp {
  def s: Stream[IO, Int] =
    Stream
      .range[IO](1, 100)
      .metered(1.seconds)
      .onFinalize(IO(println("finalized")))

  override def run(args: List[String]): IO[ExitCode] = {
    def label: String => String => Unit = { x => { y => println(s"$x: $y") } }

    InMemoryMessageBus.create[IO, Int](100)

    val res = Stream
      .awakeEvery[IO](10.seconds)
      .map(_.toSeconds.toInt)
      .debug(logger = label("A"))
      .switchMap(i =>
        Stream.range[IO](i, i + 10).metered(1.seconds).delayBy(2.seconds)
      )
    res.debug().compile.drain.as(ExitCode.Success)
  }
}

object InfiniteSwitch7 extends IOApp {
  def s: Stream[IO, Int] =
    Stream
      .range[IO](1, 100)
      .metered(1.seconds)
      .onFinalize(IO(println("finalized")))

  override def run(args: List[String]): IO[ExitCode] = {
    def label: String => String => Unit = { x => { y => println(s"$x: $y") } }

    val res = for {
      i <- Stream.eval(InMemoryMessageBus.create[IO, Int](100))
      s =
        Stream
          .awakeEvery[IO](1.seconds)
          .map(_.toSeconds.toInt)
          .evalTap(x => i.publish("A", x))
          .debug(logger = label("H"))
      r <-
        Stream
          .range[IO](1, 3)
          .metered(5.seconds)
          .debug(logger = label("T"))
          .switchMap(x => i.listen("A").debug(logger = label(s"U$x")))
          .debug(logger = label("G")) concurrently s
//      r <- i.listen("A").delayBy(3.seconds) concurrently i.listen("A").debug(logger = label("P")) concurrently s
    } yield (r)

    res.debug().compile.drain.as(ExitCode.Success)
  }
}

object InfiniteSwitch8 extends IOApp {
  def s: Stream[IO, Int] =
    Stream
      .range[IO](1, 100)
      .metered(1.seconds)
      .onFinalize(IO(println("finalized")))

  override def run(args: List[String]): IO[ExitCode] = {
    def label: String => String => Unit = { x => { y => println(s"$x: $y") } }

    val res = for {
      i <- Stream.eval(InMemoryMessageBus.create[IO, Int](100))
      s =
        Stream
          .awakeEvery[IO](1.seconds)
          .map(_.toSeconds.toInt)
          .evalTap(x => i.publish("A", x))
          .debug(logger = label("H"))
      r <-
        Stream
          .range[IO](1, 3)
          .metered(5.seconds)
          .debug(logger = label("T"))
          .switchMap(x => Stream.emit(x) ++ i.listen("A").debug(logger = label(s"U$x")))
          .debug(logger = label("G")) concurrently s
//      r <- i.listen("A").delayBy(3.seconds) concurrently i.listen("A").debug(logger = label("P")) concurrently s
    } yield (r)

    res.debug().compile.drain.as(ExitCode.Success)
  }
}
