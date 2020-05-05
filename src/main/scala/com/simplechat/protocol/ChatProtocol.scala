package com.simplechat.protocol

import akka.actor.{ActorRef, Props}
import com.simplechat.repository.ChatUsername

object ChatProtocol {
  type UserThenConnectorClosure = ActorRef => ActorRef

  sealed trait ChatProtocol
  sealed abstract class Connection(listener: ActorRef, replyTo: ActorRef) extends ChatProtocol

  sealed trait IncomingMessage extends ChatProtocol
  case class ChatMessage(msg: String) extends IncomingMessage
  case class InfoMessage(msg: String) extends IncomingMessage
  case class OutgoingMessage(msg: String) extends ChatProtocol

  case class CreateUser(user: Props, username: ChatUsername, userThenConnectorClosure: UserThenConnectorClosure) extends ChatProtocol
  case class Quit(username: ChatUsername) extends ChatProtocol
  case class Reconnect(connector: ActorRef, replyTo: ActorRef) extends ChatProtocol
  case class Reconnected(self: ActorRef, username: ChatUsername) extends ChatProtocol
  case class Joined(self: ActorRef, username: ChatUsername) extends ChatProtocol
  case class Join(listener: ActorRef, replyTo: ActorRef) extends Connection(listener, replyTo)
  case class Rejoin(listener: ActorRef, replyTo: ActorRef) extends Connection(listener, replyTo)
  case object Quit extends ChatProtocol
}
