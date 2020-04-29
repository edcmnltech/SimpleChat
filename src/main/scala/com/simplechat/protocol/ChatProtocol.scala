package com.simplechat.protocol

import akka.actor.{ActorRef, Props}
import com.simplechat.repository.ChatUsername

object ChatProtocol {
  type UserThenConnector = ActorRef => ActorRef

  sealed trait ChatProtocol

  case class CreateUser(user: Props, username: ChatUsername, userThenConnector: UserThenConnector) extends ChatProtocol
  case class IncomingMessage(msg: String) extends ChatProtocol
  case class OutgoingMessage(msg: String) extends ChatProtocol
  case class Quit(username: ChatUsername) extends ChatProtocol
  case class Joined(self: ActorRef, username: ChatUsername) extends ChatProtocol
  case class Join(listener: ActorRef) extends ChatProtocol
  case object Quit extends ChatProtocol
  case object Reconnect extends ChatProtocol
}
