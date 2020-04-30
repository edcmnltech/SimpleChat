package com.simplechat.actor

import akka.actor.{Actor, ActorLogging}
import com.simplechat.netty.Network

object ChatServerActor {
  sealed trait ServerMessage
}

class ChatServerActor extends Actor with ActorLogging {
  val network = new Network(context)

  override def preStart(): Unit = {
    network.start
    log.debug("WebSocket server started.")
  }

  override def postStop(): Unit = {
    network.stop
    log.debug("WebSocket server stopped.")
  }

  override def receive: Receive = {
    case _ => println("empty receive chat server")
  }
}
