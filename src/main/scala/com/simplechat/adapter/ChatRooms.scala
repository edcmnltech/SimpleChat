package com.simplechat.adapter

import akka.actor.{ActorContext, ActorRef, PoisonPill}
import com.simplechat.actor.ChatRoomActor
import com.simplechat.repository.{ChatRoomName, ChatRoomRepository, MySqlRepository}

import scala.collection.mutable
import scala.concurrent.Future

object ChatRooms extends ChatRoomRepository with MySqlRepository {

  /**
   * In the future ChannelGroup shall be created based on
   * the number of ChatRoom in the database.
   */

  private val chatRoomActorRefs: mutable.Map[ChatRoomName, ActorRef] = mutable.Map.empty

  private def create(actorContext: ActorContext, roomName: ChatRoomName): Future[ActorRef] = {
    selectByRoomName(roomName).map { foundRoom =>
      val actorRef = actorContext.actorOf(ChatRoomActor.props)
      val mapEntry = foundRoom.name -> actorRef
      chatRoomActorRefs += mapEntry
      actorRef
    }(ec)
  }

  def close(): Unit = {
    chatRoomActorRefs.foreach { case (_, chatRoomActorRef) =>
      chatRoomActorRef ! PoisonPill
    }
  }

  def actorRefFor(actorContext: ActorContext, roomName: ChatRoomName): Future[ActorRef] = {
    chatRoomActorRefs.find(roomName == _._1) match {
      case Some(value) => Future.successful(value._2)
      case None => create(actorContext, roomName)
    }
  }
}

