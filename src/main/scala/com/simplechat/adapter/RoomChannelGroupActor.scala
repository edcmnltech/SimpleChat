package com.simplechat.adapter

import akka.actor.{Actor, ActorRef, Props}
import com.simplechat.actor.Message
import com.simplechat.adapter.RoomChannelGroupActor.ChatRoomMessage
import com.simplechat.adapter.RoomChannelGroupActor.ChatRoomMessage.{Broadcast, Join}
import com.simplechat.adapter.UserChannelActor.ChatUserMessage
import io.netty.channel.group.ChannelGroup
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame

object RoomChannelGroupActor {
  sealed trait ChatRoomMessage
  object ChatRoomMessage {
    case class Join(user: ActorRef) extends ChatRoomMessage
    case class Broadcast(msg: String) extends ChatRoomMessage
  }

  def props(channelGroup: ChannelGroup): Props = Props(new RoomChannelGroupActor(channelGroup))
}

class RoomChannelGroupActor(channelGroup: ChannelGroup) extends Actor {
  override def receive: Receive = {

    case ChatRoomMessage.Join(user) =>
      user ! ChatUserMessage.GetUserChannel

    case ChatUserMessage.UserChannel(channel) =>
      channelGroup.add(channel)
      self ! Broadcast(Message.joined(channel.id().asShortText()))

    case ChatRoomMessage.Broadcast(message) =>
      channelGroup.writeAndFlush(new TextWebSocketFrame(message))
  }
}
