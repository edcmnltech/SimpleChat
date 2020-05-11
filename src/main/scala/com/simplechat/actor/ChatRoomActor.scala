package com.simplechat.actor

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.simplechat.protocol.ChatProtocol.{Reconnect, _}
import com.simplechat.repository.ChatUsername

import scala.collection.mutable

object ChatRoomActor {
  def props: Props = Props(new ChatRoomActor())
}

class ChatRoomActor() extends Actor with ActorLogging {

  private val users: mutable.Map[ChatUsername, ActorRef] = mutable.Map.empty
  private val chatRoomBus = new ChatRoomBusImpl

  override def receive: Receive = {

    case CreateUser(userProps, username, createConnector) => {
      checkDuplicate(username) match {
        case Some((_, oldUser)) =>
          val newConnector = createConnector(oldUser)
          oldUser ! Reconnect(newConnector, self)
        case None =>
          val (newUser, newConnector) = createNewActors(username, userProps, createConnector)
          users += (username -> newUser)
          newUser ! Join(newConnector, self)
      }
    }

    case Joined(newUser, username) => {
      context.watch(newUser)
      chatRoomBus.subscribe(newUser, "info")
      chatRoomBus.publish(IncomingMessageEnvelope("info", Message.joined(username.value)))
      //self ! Message.joined(username.value)
    }

    case Reconnected(_, username) => {
      self ! Message.reconnected(username.value)
    }

    case broadcast: IncomingMessage => {
      log.debug("Broadcasting...")
      chatRoomBus.publish(IncomingMessageEnvelope("info", broadcast))
//
//      users.foreach { case (_, user) =>
//        user ! broadcast
//      }
    }

    case Quit(username) => {
      log.debug("Quitting...")
      users.remove(username).foreach { user =>
        self ! Message.quit(username.value)
        context.unwatch(user)
        user ! Quit(username)
      }
    }

  }

  private def checkDuplicate(username: ChatUsername): Option[(ChatUsername, ActorRef)] = {
    users.find(_._1 == username)
  }

  private def createNewActors(username: ChatUsername, userProps: Props, createConnector: UserThenConnectorClosure): (ActorRef, ActorRef) = {
    val newUser: ActorRef = context.actorOf(userProps, s"user_$username")
    val newConnector: ActorRef = createConnector.apply(newUser)
    (newUser, newConnector)
  }
}
