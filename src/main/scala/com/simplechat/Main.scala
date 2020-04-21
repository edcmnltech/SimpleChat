package main.scala.com.simplechat

import akka.actor.{ActorSystem, Props}
import akka.stream.ActorMaterializer
import com.simplechat.actor.ChatServerActor

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

object Main extends App {

  def chat(): Unit = {

    implicit val system: ActorSystem = ActorSystem("simple-chat-system")
    implicit val materializer: ActorMaterializer = ActorMaterializer.create(system)
    implicit val ec: ExecutionContextExecutor = ExecutionContext.global

    system.actorOf(Props[ChatServerActor])

  }

  chat()
}