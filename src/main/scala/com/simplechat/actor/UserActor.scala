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
    case a: Joint =>
      a match {
        case Join(connector, replyTo) =>
          println("joining...")
          replyTo ! Joined(self, username)
          context.become(connected(connector))

        case Rejoin(connector, replyTo) =>
          println("rejoining...")
          replyTo ! Joined(self, username)
          context.become(connected(connector))
      }

  }

  private def connected(connector: ActorRef): Receive = {
    case IncomingMessage(msg) =>
      connector ! OutgoingMessage(msg)
    case Quit(_) =>
      connector ! PoisonPill
      self ! PoisonPill
    case Reconnect(newConnector, replyTo) =>
      println("reconnecting...")
      connector ! PoisonPill
      context.become(join)
      self ! Rejoin(newConnector, replyTo)
  }
}
