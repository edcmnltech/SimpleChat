package com.simplechat

import akka.actor.{ActorSystem, Props}
import com.simplechat.actor.ChatServerActor

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

object Main extends App {

  def chat(): Unit = {

    //val connectorSystem: ActorSystem = ActorSystem("connector-system")
    val chatServer: ActorSystem = ActorSystem("chat-server")
    implicit val ec: ExecutionContextExecutor = ExecutionContext.global

    chatServer.actorOf(Props[ChatServerActor])

  }

  chat()
}