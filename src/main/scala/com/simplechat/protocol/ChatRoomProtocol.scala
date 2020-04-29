package com.simplechat.protocol

import akka.actor.{ActorRef, Props}
import com.simplechat.repository.ChatUsername

object ChatRoomProtocol {
  sealed trait ChatRoomProtocol

  case class Create(user: Props) extends ChatRoomProtocol
  case class Broadcast(msg: String) extends ChatRoomProtocol
  case class Quit(user: ActorRef) extends ChatRoomProtocol
  case class Joined(self: ActorRef, username: ChatUsername) extends ChatRoomProtocol
  case object Join extends ChatRoomProtocol
  case object Quit extends ChatRoomProtocol
}
