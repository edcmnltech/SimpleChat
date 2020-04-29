package com.simplechat.actor

import akka.actor.{ActorContext, ActorRef}
import com.simplechat.adapter.RoomChannelGroupActor
import com.simplechat.repository.ChatRoomName
import io.netty.channel.group.{ChannelGroup, DefaultChannelGroup}
import io.netty.util.concurrent.GlobalEventExecutor

import scala.collection.mutable

object RoomChannelGroups {

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
      val actorRef = actorContext.actorOf(RoomChannelGroupActor.props)
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

  def chatRoomGroupFor(key: ChatRoomName): ChannelGroup = chatRoomActorRefs(key)._1
}

