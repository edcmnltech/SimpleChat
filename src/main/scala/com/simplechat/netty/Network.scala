package com.simplechat.netty

import java.net.{InetAddress, InetSocketAddress}

import akka.actor.ActorContext
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.group.DefaultChannelGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel.{Channel, ChannelFuture}
import io.netty.handler.logging.{LogLevel, LoggingHandler}
import io.netty.util.concurrent.ImmediateEventExecutor

object Network {
  val port = 8080
  val host = InetAddress.getByName("0.0.0.0")
  val address = new InetSocketAddress(host, port)
  //TODO: move this to other place / object
  val WS_PATH = "/ws"
}

class Network(context: ActorContext) {
  val channelGroup = new DefaultChannelGroup("clients", ImmediateEventExecutor.INSTANCE)
  val eventLoopGroup = new NioEventLoopGroup()
  val channelInitializer = new ChatServerInitializer(channelGroup, context)
  var channelFuture: ChannelFuture = _
  var channel: Channel = _

  def start: ChannelFuture = {
    val bootstrap = new ServerBootstrap()
    bootstrap.group(eventLoopGroup)
      .channel(classOf[NioServerSocketChannel])
      .handler(new LoggingHandler(LogLevel.DEBUG))
      .childHandler(channelInitializer)
    val future = bootstrap.bind(Network.address).syncUninterruptibly()
    channel = future.channel()
    channelFuture = future
    channelFuture
  }

  def stop: ChannelFuture = {
    Runtime.getRuntime.addShutdownHook(new Thread() {
      override def run(): Unit = {
        if (channel != null) {
          channel.close()
          channelGroup.close()
          eventLoopGroup.shutdownGracefully()
        }
      }
    })
    channelFuture.channel().closeFuture().syncUninterruptibly()
  }
}