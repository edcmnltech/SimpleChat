package com.simplechat.netty.helper

import com.simplechat.repository.{ChatRoomName, ChatUsername}

import scala.collection.Map
import scala.util.Try

object UrlHelper {

  val WS_PATH = "/ws"
  val ALL_CHAT_ROOMS = "/chatrooms"
  val ALL_CHAT_ROOM_PAGE_PATH = "/views/chat-rooms.html"
  val CHAT_CLIENT = "chat-client"
  val CHAT_CLIENT_PAGE = "/views/chat-client.html"
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

  def convertParamsToMap(params: List[String]): List[Map[String, String]] = {
    params.map { p =>
      val split: List[String] = p.split("=").toList
      split match {
        case k :: v if v.size == 1 => Map(k -> v.head)
      }
    }
  }

  def findValueOfParam(key: String, map: List[Map[String, String]]): Option[String] = {
    map find { _.contains(key) } map { found => found(key) }
  }

}
