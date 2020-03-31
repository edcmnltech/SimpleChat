package main.scala.com.simplechat.netty.handler

import io.netty.channel.group.ChannelGroup
import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.handler.codec.http.websocketx.{TextWebSocketFrame, WebSocketServerProtocolHandler}

class TextWebSocketFrameHandler(group: ChannelGroup) extends SimpleChannelInboundHandler[TextWebSocketFrame] {

  override def channelRead0(channelHandlerContext: ChannelHandlerContext, msg: TextWebSocketFrame): Unit = {
    group.writeAndFlush(msg)
  }

  override def userEventTriggered(ctx: ChannelHandlerContext, evt: Any): Unit = {
    if (evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {
      ctx.pipeline().remove(classOf[HttpRequestHandler])
      group.writeAndFlush(new TextWebSocketFrame("Client "+ctx.channel()+" joined"))
      group.add(ctx.channel())
    } else {
      super.userEventTriggered(ctx, evt)
    }
  }

}
