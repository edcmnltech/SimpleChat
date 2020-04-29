package com.simplechat.adapter

import akka.actor.{ActorContext, ActorRef}
import com.simplechat.actor.ChatRoomActor
import com.simplechat.repository.{ChatRoomName, ChatUsername}
import io.netty.channel.group.{ChannelGroup, DefaultChannelGroup}
import io.netty.util.concurrent.GlobalEventExecutor

import scala.collection.mutable

object ChatRooms {

  /**
   * In the future ChannelGroup shall be created based on
   * the number of ChatRoom in the database.
   */
  private val chatRooms = List(
    new DefaultChannelGroup("chat1", GlobalEventExecutor.INSTANCE),
    new DefaultChannelGroup("chat2", GlobalEventExecutor.INSTANCE)
  )

  private val chatRoomActorRefs: mutable.Map[ChatRoomName, (ChannelGroup, ActorRef)] = mutable.Map.empty

  def create(actorContext: ActorContext): Unit = {
    chatRooms.foreach { group =>
      val actorRef = actorContext.actorOf(ChatRoomActor.props)
      val mapEntry = new ChatRoomName(group.name()) -> (group, actorRef)
      chatRoomActorRefs += mapEntry
    }
  }

  def close(): Unit = {
    chatRooms.foreach { group =>
      group.close()
    }
  }

  def actorRefFor(key: ChatRoomName): ActorRef = chatRoomActorRefs(key)._2
}

