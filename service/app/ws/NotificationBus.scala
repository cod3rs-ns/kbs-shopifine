package ws

import javax.inject.Inject

import akka.actor.ActorSystem
import akka.event.{ActorEventBus, LookupClassification}
import com.google.inject.Singleton
import ws.NotificationBus.Notification

object NotificationBus {

  final case class Notification(userId: Long, message: String)

}

@Singleton
class NotificationBus @Inject()(implicit system: ActorSystem)
    extends ActorEventBus
    with LookupClassification {

  type Event      = Notification
  type Classifier = Long

  protected def mapSize(): Int = 128

  protected def classify(event: Notification): Long = event.userId

  protected def publish(event: Event, subscriber: Subscriber): Unit =
    subscriber ! event.message

}
