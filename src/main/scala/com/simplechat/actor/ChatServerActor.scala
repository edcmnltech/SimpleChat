package com.simplechat.actor

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.simplechat.actor.ChatServerActor.{Broadcast, Join, Quit}
import com.simplechat.adapter.RoomChannelGroupActor.ChatRoomMessage
import com.simplechat.netty.Network

object ChatServerActor {
  sealed trait ServerMessage
  case class Join(chatRoom: ActorRef, user: ActorRef) extends ServerMessage
  case class Broadcast(chatRoom: ActorRef, message: String) extends ServerMessage
  case class Quit(chatRoom: ActorRef, user: ActorRef) extends ServerMessage
}

class ChatServerActor extends Actor with ActorLogging {
  val network = new Network(context)

  override def preStart(): Unit = {
    network.start
    log.debug("WebSocket server started.")
  }

  override def postStop(): Unit = {
    network.stop
    log.debug("WebSocket server stopped.")
  }

  override def receive: Receive = {
    case Join(chatRoom, user) =>
      chatRoom ! ChatRoomMessage.Join(user)

    case Broadcast(chatRoom, message) =>
      chatRoom ! ChatRoomMessage.Broadcast(message)

    case Quit(_, _) =>
      log.debug(s"WebSocket user quit the server.")
      network.stop
  }
}
