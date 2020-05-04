package com.simplechat

import akka.actor.{ActorSystem, Props}
import akka.stream.{ActorMaterializer, Materializer}
import com.simplechat.actor.ChatServerActor
import com.simplechat.rest.RestServer

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

object Main extends App {

  def chat(): Unit = {

    //val connectorSystem: ActorSystem = ActorSystem("connector-system")
    val chatServer: ActorSystem = ActorSystem("chat-server")
    implicit val ec: ExecutionContextExecutor = ExecutionContext.global
    implicit val mat: Materializer = ActorMaterializer.create(chatServer)

    chatServer.actorOf(Props[ChatServerActor])
    new RestServer(chatServer).start

  }

  chat()
}