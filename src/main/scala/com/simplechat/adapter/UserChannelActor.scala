package com.simplechat.adapter

import akka.actor.{Actor, Props}
import com.simplechat.adapter.UserChannelActor.ChatUserMessage.{GetUserChannel, UserChannel}
import io.netty.channel.Channel

object UserChannelActor {
  sealed trait ChatUserMessage
  object ChatUserMessage {
    case object GetUserChannel extends ChatUserMessage
    case class UserChannel(channel: Channel) extends ChatUserMessage
  }

  def props(channel: Channel): Props = Props(new UserChannelActor(channel))
}

class UserChannelActor(channel: Channel) extends Actor {
  override def receive: Receive = {
    case GetUserChannel =>
      sender ! UserChannel(channel)
  }
}
