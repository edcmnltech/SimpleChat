package com.simplechat.netty.handler

import akka.actor.{ActorRef, Props}
import com.simplechat.protocol.ChatRoomProtocol._
import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.handler.codec.http.websocketx.{TextWebSocketFrame, WebSocketServerProtocolHandler}

class ChatWebSocketFrameHandler(chatRoom: ActorRef, userProps: Props) extends SimpleChannelInboundHandler[TextWebSocketFrame] {

  override def userEventTriggered(ctx: ChannelHandlerContext, evt: Any): Unit = {
    if (evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {
      chatRoom ! Create(userProps)
    } else {
      super.userEventTriggered(ctx, evt)
    }
  }

  override def channelRead0(ctx: ChannelHandlerContext, msg: TextWebSocketFrame): Unit = {
    chatRoom ! Broadcast(msg.text())
  }

}
