package main.scala.com.simplechat.netty

import java.net.InetSocketAddress

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.group.DefaultChannelGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel.{Channel, ChannelFuture}
import io.netty.handler.logging.{LogLevel, LoggingHandler}
import io.netty.util.concurrent.ImmediateEventExecutor

class ChatServer {
  val channelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE)
  val eventLoopGroup = new NioEventLoopGroup()
  var channel: Channel = _

  lazy val channelInitializer = new ChatServerInitializer(channelGroup)

  def start(address: InetSocketAddress): ChannelFuture = {
    val bootstrap = new ServerBootstrap()
    bootstrap.group(eventLoopGroup)
      .channel(classOf[NioServerSocketChannel])
      .handler(new LoggingHandler(LogLevel.DEBUG))
      .childHandler(channelInitializer)
    val future = bootstrap.bind(address)
    future.syncUninterruptibly()
    channel = future.channel()
    future
  }

  def destroy(): Unit = {
    if (channel != null) channel.close()
    channelGroup.close()
    eventLoopGroup.shutdownGracefully()
  }
}