package com.simplechat.adapter

import akka.actor.{Actor, Props}
import com.simplechat.adapter.UserChannelActor.ChatUserMessage.{JoinChannel, JoinRequest, QuitChannel, QuitRequest}
import io.netty.channel.Channel

object UserChannelActor {
  sealed trait ChatUserMessage
  object ChatUserMessage {
    case object JoinRequest extends ChatUserMessage
    case object QuitRequest extends ChatUserMessage
    case class JoinChannel(channel: Channel) extends ChatUserMessage
    case class QuitChannel(channel: Channel) extends ChatUserMessage
  }

  def props(channel: Channel): Props = Props(new UserChannelActor(channel))
}

class UserChannelActor(channel: Channel) extends Actor {
  override def receive: Receive = {
    case JoinRequest => sender ! JoinChannel(channel)
    case QuitRequest => sender ! QuitChannel(channel)
  }
}
