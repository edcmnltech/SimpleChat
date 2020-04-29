package com.simplechat.adapter

import akka.actor.{Actor, Props}
import com.simplechat.protocol.ChatRoomProtocol.Broadcast
import io.netty.channel.Channel
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame

object ConnectionActor {
  def props(channel: Channel): Props = Props(new ConnectionActor(channel))
}

class ConnectionActor(channel: Channel) extends Actor {
  override def receive: Receive = {
    case Broadcast(msg) => channel.writeAndFlush(new TextWebSocketFrame(msg))
//    case QuitRequest => sender ! QuitChannel(self)
  }
}
