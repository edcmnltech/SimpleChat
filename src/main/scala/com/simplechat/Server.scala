package main.scala.com.simplechat

import java.net.{InetAddress, InetSocketAddress}

import main.scala.com.simplechat.netty.ChatServer

object Server extends App {

  def chat(): Unit = {

    val port = 8080
    val host = InetAddress.getByName("0.0.0.0")
    val endpoint = new ChatServer()
    val future = endpoint.start(new InetSocketAddress(host, port))

    Runtime.getRuntime.addShutdownHook(new Thread() {
      override def run(): Unit = endpoint.destroy()
    })

    future.channel().closeFuture().syncUninterruptibly()
  }

  chat()
}