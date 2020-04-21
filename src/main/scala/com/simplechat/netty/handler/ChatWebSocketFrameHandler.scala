package com.simplechat.netty.handler

import akka.actor.{ActorContext, ActorRef}
import com.simplechat.actor.ChatServerActor.{Broadcast, Join, Quit}
import com.simplechat.netty.AttrHelper
import io.netty.channel.group.ChannelGroup
import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.handler.codec.http.websocketx.{TextWebSocketFrame, WebSocketServerProtocolHandler}

class ChatWebSocketFrameHandler(group: ChannelGroup, actorContext: ActorContext) extends SimpleChannelInboundHandler[TextWebSocketFrame] {

  val chatServer: ActorRef = actorContext.self

  override def userEventTriggered(ctx: ChannelHandlerContext, evt: Any): Unit = {
    if (evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {
      //ctx.pipeline().remove(classOf[HttpRequestHandler])
      chatServer ! Join(group, ctx.channel())
    } else {
      super.userEventTriggered(ctx, evt)
    }
  }

  override def channelRead0(ctx: ChannelHandlerContext, msg: TextWebSocketFrame): Unit = {
    val sender = AttrHelper.getUsername(ctx.channel())
    val room = AttrHelper.getChatRoom(ctx.channel())
    chatServer ! Broadcast(group, s"${sender.value}: ${msg.text()}", room)
  }

  override def channelInactive(ctx: ChannelHandlerContext): Unit =
    chatServer ! Quit(group, ctx.channel())

}
