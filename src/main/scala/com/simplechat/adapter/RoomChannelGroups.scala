package com.simplechat.actor

import io.netty.channel.group.DefaultChannelGroup
import io.netty.util.concurrent.GlobalEventExecutor

object RoomChannelGroups {

  /**
   * In the future ChannelGroup shall be created based on
   * the number of ChatRoom in the database.
   */
  val chatRooms = List(
    new DefaultChannelGroup("chat1", GlobalEventExecutor.INSTANCE),
    new DefaultChannelGroup("chat2", GlobalEventExecutor.INSTANCE)
  )
}
