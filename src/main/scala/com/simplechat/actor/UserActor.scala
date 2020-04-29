package com.simplechat.actor

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.simplechat.protocol.ChatRoomProtocol._
import com.simplechat.repository.ChatUsername

object UserActor {
  def props(connActorProps: Props, username: ChatUsername) = Props(new UserActor(connActorProps, username))
}

class UserActor(connActorProps: Props, username: ChatUsername) extends Actor with ActorLogging {

  val network: ActorRef = context.actorOf(connActorProps)

  override def receive: Receive = {
    case Join =>
      sender ! Joined(self, username)
      context.become(joined)
  }

  private def joined: Receive = {
    case broadcast @ Broadcast(_) =>
      network ! broadcast
  }

}
