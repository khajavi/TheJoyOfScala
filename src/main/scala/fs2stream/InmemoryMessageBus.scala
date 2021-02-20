package fs2stream

package info.particleb.exchanges.binance.stream

import cats.effect.{ConcurrentEffect, Sync, Timer}
import cats.effect.concurrent.Ref
import cats.instances.list._
import cats.syntax.flatMap._
import cats.syntax.functor._
import cats.syntax.traverse._
import fs2.Stream
import fs2.concurrent.Queue
import info.particleb.exchanges.binance.stream.InMemoryMessageBus.State
import fs2.Stream

import scala.concurrent.duration.DurationInt

trait MessageBus[F[_], T] {

  /**
    * Publishes message on specified topic
    *
    * @param topic Topic to publish on
    * @param data  Message to publish
    * @return
    */
  def publish(topic: String, data: T): F[Unit]

  /**
    * Listens and get a stream of all the messages published on this Topic after listening.
    *
    * @param topic Topic to listen on
    * @return
    */
  def listen(topic: String): Stream[F, T]
}

final class InMemoryMessageBus[F[_]: ConcurrentEffect: Timer, T](
    subscriptions: State[F, T],
    bufferSize: Int
) extends MessageBus[F, T] {

  override def publish(topic: String, data: T): F[Unit] =
    subscriptions.get
      .map(_.get(topic))
      .flatMap {
        case Some(q) =>
          q
            .traverse(_.enqueue1(data))
            .void
        case None => Sync[F].unit
      }

  private def addSubscription(str: String, queue: Queue[F, T]): F[Unit] =
    subscriptions.update { s =>
      val topicSubs = s.get(str) match {
        case Some(subs) => subs :+ queue
        case None       => List(queue)
      }
      s.updated(str, topicSubs)
    }

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  private def removeSubscription(topic: String, queue: Queue[F, T]): F[Unit] =
    subscriptions.update { s =>
      s.get(topic).map(_.filterNot(_ == queue)) match {
        case Some(value) => s.updated(topic, value)
        case None        => s
      }
    }

  override def listen(topic: String): Stream[F, T] =
    for {
      q <- Stream.eval(Queue.bounded[F, T](bufferSize))
      _ <- Stream.eval(addSubscription(topic, q))
      out <- q.dequeue.onFinalize(removeSubscription(topic, q)).interruptAfter(10.seconds).onFinalize(Sync[F].delay(println("finalized")))
    } yield out
}

object InMemoryMessageBus {
  private type State[F[_], T] = Ref[F, Map[String, List[Queue[F, T]]]]

  /**
    * Creates an in memory message bus that can publish and listen to messages of type T
    *
    * @tparam F Effect type
    * @tparam T Message type
    * @return
    */
  def create[F[_]: ConcurrentEffect: Timer, T](
      bufferSize: Int
  ): F[MessageBus[F, T]] =
    for {
      subscriptions <- Ref.of[F, Map[String, List[Queue[F, T]]]](Map.empty)
    } yield new InMemoryMessageBus[F, T](subscriptions, bufferSize)
}
