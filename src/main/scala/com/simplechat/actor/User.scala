package com.simplechat.actor

import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill}
import akka.stream.CompletionStrategy
import com.simplechat.actor.User.{Connected, IncomingMessage, OutgoingMessage}
import com.simplechat.repository.{ChatRoomActorRef, ChatUsername}

import scala.concurrent.{ExecutionContext, Future}

object User {
  final case class Connected(outgoing: ActorRef)
  final case class IncomingMessage(text: String, sender: ChatUsername)
  final case class OutgoingMessage(text: String, sender: ChatUsername)
}

class User(a: String)/**(chatRoom: ChatRoomActorRef, userName: ChatUserName)**/(implicit ec: ExecutionContext) extends Actor with ActorLogging {

  def receive: Receive = {
    case msg => println(s"Sample use $a $msg")
//    case Connected(outgoing) =>
//      log.info(s"User connected: ${userName.value}")
//      context.become(connected(outgoing))
//      chatRoom.actorRef ! Room.Join
  }
//
//  def connected(outgoing: ActorRef): Receive = {
//    case IncomingMessage(text, sender) =>
//      log.info(s"Msg in <- ${chatRoom.actorRef}")
//      chatRoom.actorRef ! Room.ChatMessage(text, sender)
//    case a: Future[Seq[IncomingMessage]] =>
//      a.map{ xxx =>
//        xxx.map { msg =>
//          log.info(s"Msg in <- ${chatRoom.actorRef}")
//          chatRoom.actorRef ! Room.ChatMessage(msg.text, msg.sender)
//        }
//      }
//    case Room.ChatMessage(text, sender) =>
//      log.info(s"Msg out -> $outgoing")
//      outgoing ! OutgoingMessage(text, sender)
//    case _: CompletionStrategy =>
//      self ! PoisonPill
//    case wth =>
//      log.error(s"ERROR with: $wth")
//  }
//
//  override def postStop(): Unit = {
//    log.info("Bye world!")
//    super.postStop()
//  }
}
