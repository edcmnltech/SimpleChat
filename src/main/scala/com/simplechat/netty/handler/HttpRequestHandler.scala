package com.simplechat.netty.handler

import akka.actor.ActorContext
import com.simplechat.netty.{AttrHelper, Network}
import com.simplechat.repository.{ChatRoom, ChatRoomName, ChatRoomRepository, ChatUser, ChatUserRepository, ChatUsername, MySqlRepository}
import io.netty.channel._
import io.netty.channel.group.ChannelGroup
import io.netty.handler.codec.http._
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler

import scala.concurrent.Await
import scala.concurrent.duration._

class HttpRequestHandler(wsUri: String, group: ChannelGroup, context: ActorContext) extends SimpleChannelInboundHandler[FullHttpRequest]
  with MySqlRepository
  with ChatUserRepository
  with ChatRoomRepository {

  override def channelRead0(ctx: ChannelHandlerContext, request: FullHttpRequest): Unit = {

    if (request.uri().contains(Network.WS_PATH)) {
      if (request.uri().contains("/user")) {
        val username = getUsername(request.uri())
        val validUser: ChatUser = Await.result(selectByUsername(username), 2.seconds)
        AttrHelper.setUsername(ctx.channel(), validUser.username)

        if (request.uri().contains("/room")) {
          val chatRoom = getChatRoom(request.uri())
          val validRoom: ChatRoom = Await.result(selectByRoomName(chatRoom), 2.seconds)
          AttrHelper.setChatRoom(ctx.channel(), validRoom.name)

          // ctx.pipeline().addAfter(ctx.name(), s"WebSocketServerProtocolHandler#0", new WebSocketServerProtocolHandler(request.uri()))
          ctx.pipeline().addLast(new WebSocketServerProtocolHandler(request.uri()))
          ctx.pipeline().addLast(new ChatWebSocketFrameHandler(group, context))
          ctx.fireChannelRead(request.retain())
        } else {
          println("no chat room found in url.")
          ctx.close()
        }
      } else {
        println("no user found in url.")
        ctx.close()
      }
    } else {
      println("invalid ws url.")
      ctx.close()
    }
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    throw new Exception {
      cause.printStackTrace()
      ctx.close()
    }
  }

  protected def getUrlParams(uri: String): List[String] = {
    val params = uri.split("\\?")
    if (params.size != 1) {
      params.tail.flatMap(_.split("&")).toList
    } else List.empty[String]
  }

  protected def getUsername(uri: String): ChatUsername = {
    val params = uri.split("/user/")
    if (params.size != 1) {
      val username = params.last.split("/").head
      new ChatUsername(username)
    } else new ChatUsername("")
  }

  protected def getChatRoom(uri: String): ChatRoomName = {
    val params = uri.split("/room/")
    if (params.size != 1) {
      new ChatRoomName(params.last)
    } else new ChatRoomName("")
  }

}
