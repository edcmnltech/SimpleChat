package main.scala.com.simplechat.netty

import io.netty.channel.group.ChannelGroup
import io.netty.channel.{Channel, ChannelInitializer}
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler
import io.netty.handler.codec.http.{HttpObjectAggregator, HttpServerCodec}
import io.netty.handler.stream.ChunkedWriteHandler
import main.scala.com.simplechat.netty.handler.{HttpRequestHandler, TextWebSocketFrameHandler}

class ChatServerInitializer(group: ChannelGroup) extends ChannelInitializer[Channel] {

  override def initChannel(c: Channel): Unit = {
    val pipeline = c.pipeline()
    pipeline.addLast(new HttpServerCodec())
    pipeline.addLast(new ChunkedWriteHandler())
    pipeline.addLast(new HttpObjectAggregator(64 * 1024))
    pipeline.addLast(new HttpRequestHandler("/ws"))
    pipeline.addLast(new WebSocketServerProtocolHandler("/ws"))
    pipeline.addLast(new TextWebSocketFrameHandler(group))
  }

}
