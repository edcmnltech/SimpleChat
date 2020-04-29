package com.simplechat.adapter

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.simplechat.protocol.ChatProtocol.OutgoingMessage
import com.simplechat.repository.ChatUsername
import io.netty.channel.Channel
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame

object ConnectionActor {
  def props(channel: Channel, username: ChatUsername)(userActorRef: ActorRef): Props =
    Props(new ConnectionActor(channel, username, userActorRef))
}

class ConnectionActor(channel: Channel, username: ChatUsername, userActorRef: ActorRef) extends Actor with ActorLogging {

  override def receive: Receive = {
    case OutgoingMessage(msg) =>
      channel.writeAndFlush(new TextWebSocketFrame(msg))
  }
}
