package com.simplechat.netty

import akka.actor.ActorRef
import com.simplechat.repository.{ChatRoomName, ChatUsername}
import io.netty.channel.Channel
import io.netty.util.AttributeKey

object AttrHelper {
  def setUsername(channel: Channel, user: ChatUsername): Unit = {
    channel.attr(AttributeKey.newInstance[ChatUsername](s"username@${channel.id()}")).set(user)
  }

  def getUsername(channel: Channel): ChatUsername = {
    channel.attr(AttributeKey.valueOf(s"username@${channel.id()}")).get()
  }

  def setUserActorRef(channel: Channel, user: ActorRef): Unit = {
    channel.attr(AttributeKey.newInstance[ActorRef](s"actorRef@${channel.id()}")).set(user)
  }

  def getUserActorRef(channel: Channel): ActorRef = {
    channel.attr(AttributeKey.valueOf(s"actorRef@${channel.id()}")).get()
  }

  def setChatRoom(channel: Channel, room: ChatRoomName): Unit = {
    channel.attr(AttributeKey.newInstance[ChatRoomName](s"room@${channel.id()}")).set(room)
  }

  def getChatRoom(channel: Channel): ChatRoomName = {
    channel.attr(AttributeKey.valueOf(s"room@${channel.id()}")).get()
  }
}
