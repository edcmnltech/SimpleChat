//package com.simplechat.netty.handler
//
//import io.netty.channel.group.ChannelGroup
//import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
//import io.netty.handler.codec.http.websocketx.{TextWebSocketFrame, WebSocketFrame, WebSocketServerProtocolHandler}
//
//class TextWebSocketFrameHandler(group: ChannelGroup) extends SimpleChannelInboundHandler[TextWebSocketFrame] {
//
//  override def channelRead0(ctx: ChannelHandlerContext, msg: TextWebSocketFrame): Unit = {
//    msg match {
//      case msgFrame: WebSocketFrame =>
//        msgFrame match {
//          case _: TextWebSocketFrame =>
//            println(s"WebSocket Message: ${msg.text()}")
//            group.writeAndFlush(msg)
//          case _ =>
//            println("WebSocket Closing")
//            group.writeAndFlush(msg)
//            ctx.close()
//        }
//      case _ => //nothing to do
//    }
//  }
//
//  override def userEventTriggered(ctx: ChannelHandlerContext, evt: Any): Unit = {
//    if (evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {
//      ctx.pipeline().remove(classOf[HttpRequestHandler])
//      val info = "WebSocket Client "+ctx.channel()+" joined"
//      println(info)
//
//      group.writeAndFlush(new TextWebSocketFrame(info))
//      group.add(ctx.channel())
//    } else {
//      super.userEventTriggered(ctx, evt)
//    }
//  }
//
//  override def channelInactive(ctx: ChannelHandlerContext): Unit = {
//    println("WebSocket Client disconnected!")
//  }
//
//}
