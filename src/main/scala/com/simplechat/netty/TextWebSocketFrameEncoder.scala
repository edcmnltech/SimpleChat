package com.simplechat.netty

import java.util

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageEncoder
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame

class TextWebSocketFrameEncoder extends MessageToMessageEncoder[String] {
  override def encode(channelHandlerContext: ChannelHandlerContext, msg: String, list: util.List[AnyRef]): Unit = {
    list.add(new TextWebSocketFrame(msg))
  }
}
