package com.simplechat.netty

import com.simplechat.repository.{ChatRoomName, ChatUsername}

object UrlHelper {

  val WS_PATH = "/ws"
  val USER_PATH = "/user/"
  val ROOM_PATH = "/room/"

  def getUrlParams(uri: String): List[String] = {
    val params = uri.split("\\?")
    if (params.size != 1) {
      params.tail.flatMap(_.split("&")).toList
    } else List.empty[String]
  }

  def getChatUsername(uri: String): ChatUsername = {
    val params = uri.split(USER_PATH)
    if (params.size != 1) {
      val username = params.last.split("/").head
      new ChatUsername(username)
    } else new ChatUsername("")
  }

  def getChatRoomName(uri: String): ChatRoomName = {
    val params = uri.split(ROOM_PATH)
    if (params.size != 1) {
      new ChatRoomName(params.last)
    } else new ChatRoomName("")
  }

}
