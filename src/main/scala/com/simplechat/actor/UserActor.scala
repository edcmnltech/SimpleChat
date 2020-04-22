package com.simplechat.actor

import akka.actor.{Actor, ActorLogging, Props}
import com.simplechat.actor.UserActor.Joined
import com.simplechat.repository.ChatUsername

object UserActor {
  sealed trait UserMessage
  case object Joined
  case class ChatMessage(msg: String)

  def props(username: ChatUsername) = Props(new UserActor(username))
}

class UserActor(username: ChatUsername) extends Actor with ActorLogging {

  override def receive: Receive = {
    case Joined =>
      log.debug("User client connected to WebSocket server")
  }

}
