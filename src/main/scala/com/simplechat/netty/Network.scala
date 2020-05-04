package com.simplechat.netty

import java.net.{InetAddress, InetSocketAddress}

import akka.actor.ActorContext
import com.simplechat.adapter.ChatRooms
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel.{Channel, ChannelFuture}
import io.netty.handler.logging.{LogLevel, LoggingHandler}

object Network {
  val port = 8080
  val restPort = 8081
  val host = InetAddress.getByName("0.0.0.0")
  val address = new InetSocketAddress(host, port)
}

class Network(context: ActorContext) {
  val eventLoopGroup = new NioEventLoopGroup()
  val channelInitializer = new ChatUserChannelInitializer(context)
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
          ChatRooms.close()
          eventLoopGroup.shutdownGracefully()
        }
      }
    })
    channelFuture.channel().closeFuture().syncUninterruptibly()
  }
}