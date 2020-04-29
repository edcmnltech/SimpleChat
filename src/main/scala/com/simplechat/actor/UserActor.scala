package com.simplechat.actor

import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill, Props}
import com.simplechat.protocol.ChatProtocol._
import com.simplechat.repository.ChatUsername

object UserActor {
  def props(username: ChatUsername) = Props(new UserActor(username))
}

class UserActor(username: ChatUsername) extends Actor with ActorLogging {

  override def receive: Receive = join

  private def join: Receive = {
    case Join(connector) =>
      sender ! Joined(self, username)
      context.become(connected(connector))
  }

  private def connected(connector: ActorRef): Receive = {
    case IncomingMessage(msg) =>
      connector ! OutgoingMessage(msg)
    case Reconnect =>
      context.become(join)
    case Quit(_) =>
      connector ! PoisonPill
      self ! PoisonPill
  }
}
