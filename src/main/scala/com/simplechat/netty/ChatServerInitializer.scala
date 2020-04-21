package com.simplechat.netty

import akka.actor.ActorContext
import com.simplechat.netty.handler.HttpRequestHandler
import io.netty.channel.group.ChannelGroup
import io.netty.channel.{Channel, ChannelInitializer}
import io.netty.handler.codec.http.{HttpObjectAggregator, HttpServerCodec}
import io.netty.handler.stream.ChunkedWriteHandler

class ChatServerInitializer(group: ChannelGroup, context: ActorContext) extends ChannelInitializer[Channel] {

  override def initChannel(c: Channel): Unit = {
    val pipeline = c.pipeline()
    pipeline.addLast(new HttpServerCodec())
    pipeline.addLast(new ChunkedWriteHandler())
    pipeline.addLast(new HttpObjectAggregator(64 * 1024))
    pipeline.addLast(new HttpRequestHandler(Network.WS_PATH, group, context))
  }

}
