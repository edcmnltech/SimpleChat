package com.simplechat.protocol

import akka.actor.{ActorRef, Props}
import com.simplechat.repository.ChatUsername

object ChatProtocol {
  type UserThenConnector = ActorRef => ActorRef

  sealed trait ChatProtocol
  sealed abstract class Joint(listener: ActorRef, replyTo: ActorRef) extends ChatProtocol

  case class CreateUser(user: Props, username: ChatUsername, userThenConnector: UserThenConnector) extends ChatProtocol
  case class IncomingMessage(msg: String) extends ChatProtocol
  case class OutgoingMessage(msg: String) extends ChatProtocol
  case class Quit(username: ChatUsername) extends ChatProtocol
  case class Reconnect(connector: ActorRef, replyTo: ActorRef) extends ChatProtocol
  case class Reconnected(self: ActorRef, username: ChatUsername) extends ChatProtocol
  case class Joined(self: ActorRef, username: ChatUsername) extends ChatProtocol
  case class Join(listener: ActorRef, replyTo: ActorRef) extends Joint(listener, replyTo)
  case class Rejoin(listener: ActorRef, replyTo: ActorRef) extends Joint(listener, replyTo)
  case object Quit extends ChatProtocol
  case object Reconnect extends ChatProtocol
}
