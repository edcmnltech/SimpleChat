package com.simplechat.netty.handler

import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.handler.codec.http.FullHttpRequest

class LoginHandler extends SimpleChannelInboundHandler[FullHttpRequest]{

  override def channelRead0(ctx: ChannelHandlerContext, request: FullHttpRequest): Unit = {
    if (request.uri() == "/ws/login") {
      println("LOGIN HANDLER")
    }
    ctx.fireChannelRead(request.retain())
  }
}
