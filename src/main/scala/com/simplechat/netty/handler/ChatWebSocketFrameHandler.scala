package com.simplechat.netty.handler

import akka.actor.{ActorContext, ActorRef, Props}
import com.simplechat.adapter.ConnectionActor
import com.simplechat.netty.helper.AttrHelper
import com.simplechat.protocol.ChatProtocol._
import com.simplechat.repository.ChatUsername
import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.handler.codec.http.websocketx.{TextWebSocketFrame, WebSocketServerProtocolHandler}

class ChatWebSocketFrameHandler(chatRoom: ActorRef, userProps: Props, username: ChatUsername, actorContext: ActorContext) extends SimpleChannelInboundHandler[TextWebSocketFrame] {

  override def userEventTriggered(ctx: ChannelHandlerContext, evt: Any): Unit = {
    if (evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {
      val createConnectorClosure: UserThenConnectorClosure = userActorRef => {
        val connActorProps = ConnectionActor.props(ctx.channel(), username)(userActorRef)
        val connActorName = s"connector_${ctx.channel.id()}_${username.value}"
        val connActorRef = actorContext.actorOf(connActorProps, connActorName)
        AttrHelper.setUsername(ctx.channel(), username)
        connActorRef
      }
      chatRoom ! CreateUser(userProps, username, createConnectorClosure)
    } else {
      super.userEventTriggered(ctx, evt)
    }

  }

  override def channelRead0(ctx: ChannelHandlerContext, msg: TextWebSocketFrame): Unit = {
    chatRoom ! IncomingMessage(msg.text())
  }

  override def channelUnregistered(ctx: ChannelHandlerContext): Unit = {
    println(s"unregistering... ${ctx.channel().id()}")
    ctx.close()
  }

}
