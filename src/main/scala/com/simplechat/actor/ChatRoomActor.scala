package com.simplechat.actor

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.simplechat.protocol.ChatProtocol._
import com.simplechat.repository.ChatUsername

import scala.collection.mutable

object ChatRoomActor {
  def props: Props = Props(new ChatRoomActor())
}

class ChatRoomActor() extends Actor with ActorLogging {

  private val users: mutable.Map[ChatUsername, ActorRef] = mutable.Map.empty

  override def receive: Receive = {

    case CreateUser(userProps, username, createConnector) => {
      val newUser = context.actorOf(userProps, s"user_${username.value}")
      val connector = createConnector(newUser)
      newUser ! Join(connector)
    }

    case Joined(newUser, username) => {
      users += (username -> newUser)
      context.watch(newUser)
      self ! IncomingMessage(Message.joined(username.value))
    }

    case broadcast @ IncomingMessage(_) => {
      log.debug("Broadcasting...")
      users.foreach { case (_, user) =>
        user ! broadcast
      }
    }

    case Quit(username) => {
      log.debug("Quitting...")
      users.remove(username).foreach { user =>
        self ! IncomingMessage(Message.quit(username.value))
        context.unwatch(user)
        user ! Quit(username)
      }
    }

  }
}
