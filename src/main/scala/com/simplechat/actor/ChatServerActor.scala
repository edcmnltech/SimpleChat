package com.simplechat.actor

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.simplechat.actor.ChatServerActor.{Broadcast, Join, Quit}
import com.simplechat.actor.UserActor.ChatMessage
import com.simplechat.netty.{AttrHelper, Network}
import com.simplechat.repository.{ChatRoom, ChatRoomName}
import io.netty.channel.Channel
import io.netty.channel.group.ChannelGroup

object ChatServerActor {
  sealed trait ServerMessage
  case class Join(group: ChannelGroup, channel: Channel) extends ServerMessage
  case class Quit(group: ChannelGroup, channel: Channel) extends ServerMessage
  case class Broadcast(group: ChannelGroup, message: String, room: ChatRoomName) extends ServerMessage
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
    case Join(group, channel) =>
      log.debug(s"WebSocket user: $channel joined the server.")
      group.add(channel)
      val userActorRef = context.actorOf(Props[UserActor])
      AttrHelper.setUserActorRef(channel, userActorRef)
      val username = AttrHelper.getUsername(channel)
      val room = AttrHelper.getChatRoom(channel)
      self ! Broadcast(group, s"${username.value} joined", room)

    case Broadcast(group, message, chatRoom) =>
      group.forEach { channel =>
        if (AttrHelper.getChatRoom(channel) == chatRoom) {
          val user: ActorRef = AttrHelper.getUserActorRef(channel)
          user ! ChatMessage(channel, s"MODIFIED $message")
        }
      }

    case Quit(group, channel) =>
      log.debug(s"WebSocket user: $channel quit the server.")
      group.remove(channel)
  }
}
