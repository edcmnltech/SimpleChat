package com.simplechat.actor

import akka.actor.{Actor, ActorLogging}
import com.simplechat.actor.UserActor.{ChatMessage, Joined}
import io.netty.channel.Channel
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame

object UserActor {
  sealed trait UserMessage
  case object Joined
  case class ChatMessage(channel: Channel, msg: String)
}

class UserActor extends Actor with ActorLogging {

  override def receive: Receive = {
    case Joined =>
      log.debug("User client connected to WebSocket server")

    case ChatMessage(channel, msg) =>
      log.debug(msg)
      channel.writeAndFlush(new TextWebSocketFrame(msg))
  }

}
