package ws

import akka.actor._

object Connector {

  def props(userId: Option[Long], bus: NotificationBus, out: ActorRef) =
    Props(new Connector(userId, bus, out))

}

class Connector(userId: Option[Long], bus: NotificationBus, out: ActorRef)
    extends Actor {

  override def preStart(): Unit = userId match {
    case Some(id) => bus.subscribe(self, id)
    case None     => self ! PoisonPill
  }

  def receive: Receive = {
    case msg: String =>
      out ! msg
  }

}
