package com.simplechat.adapter

import akka.actor.{Actor, ActorRef, Props}
import com.simplechat.actor.Message
import com.simplechat.protocol.ChatRoomProtocol._

import scala.collection.mutable.ListBuffer

object RoomChannelGroupActor {
  def props: Props = Props(new RoomChannelGroupActor())
}

class RoomChannelGroupActor() extends Actor {

  private val users: ListBuffer[ActorRef] = ListBuffer.empty[ActorRef]

  override def receive: Receive = {

    case Create(userProps) => {
      val user = context.actorOf(userProps)
      user ! Join
    }

    case Joined(newUser, username) => {
      users += newUser
      context.watch(newUser)
      self ! Broadcast(Message.joined(username.value))
    }

    case broadcast @ Broadcast(_) => {
      users.foreach { user =>
        user ! broadcast
      }
    }
//
//    case ChatRoomMessage.Quit(user) => {
//      user ! ChatUserMessage.QuitRequest
//    }
//
//    case ChatUserMessage.QuitChannel(channel, user) => {
//      channelGroup.remove(channel)
//      user ! PoisonPill
//      self ! Broadcast(Message.quit(channel.id().asShortText()))
//    }

  }
}
