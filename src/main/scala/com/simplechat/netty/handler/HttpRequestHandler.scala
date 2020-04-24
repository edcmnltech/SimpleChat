package com.simplechat.netty.handler

import akka.actor.ActorContext
import com.simplechat.actor.RoomChannelGroups
import com.simplechat.adapter.{RoomChannelGroupActor, UserChannelActor}
import com.simplechat.netty.{AttrHelper, UrlHelper}
import com.simplechat.repository._
import io.netty.channel._
import io.netty.channel.group.{ChannelGroup, DefaultChannelGroup}
import io.netty.handler.codec.http._
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler
import io.netty.util.concurrent.GlobalEventExecutor

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

class HttpRequestHandler(wsUri: String, actorContext: ActorContext) extends SimpleChannelInboundHandler[FullHttpRequest]
  with MySqlRepository
  with ChatUserRepository
  with ChatRoomRepository {

  override def channelRead0(ctx: ChannelHandlerContext, request: FullHttpRequest): Unit = {

    if (request.uri().contains(UrlHelper.WS_PATH)) {
      if (request.uri().contains(UrlHelper.USER_PATH)) {
        if (request.uri().contains(UrlHelper.ROOM_PATH)) {

          val validChatRoom: ChatRoom = extractChatRoom(ctx, request)
          val validChatUser: ChatUser = extractChatUser(ctx, request)
          //At this point, it should be guaranteed that every chat room has
          //corresponding ChannelGroup
          val chatRoomChannelGroup = RoomChannelGroups.chatRooms.find(_.name() == validChatRoom.name.value).getOrElse(new DefaultChannelGroup(GlobalEventExecutor.INSTANCE))
          val chatRoomActorRef = actorContext.actorOf(RoomChannelGroupActor.props(chatRoomChannelGroup))
          val chatUserActorRef = actorContext.actorOf(UserChannelActor.props(ctx.channel()))

          replaceOldUserChannel(chatRoomChannelGroup, validChatUser)
          addUsernameToChanne(ctx.channel(), validChatUser)
          
          ctx.pipeline().addLast(new WebSocketServerProtocolHandler(request.uri()))
          ctx.pipeline().addLast(new ChatWebSocketFrameHandler(actorContext, chatRoomActorRef, chatUserActorRef))
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

  protected def extractChatUser(ctx: ChannelHandlerContext, request: FullHttpRequest): ChatUser = {
    val chatUser = UrlHelper.getChatUsername(request.uri())
    Await.result(selectByUsername(chatUser), 2.seconds)
  }

  protected def extractChatRoom(ctx: ChannelHandlerContext, request: FullHttpRequest): ChatRoom = {
    val chatRoom = UrlHelper.getChatRoomName(request.uri())
    Await.result(selectByRoomName(chatRoom), 2.seconds)
  }

  protected def replaceOldUserChannel(chatRoomChannelGroup: ChannelGroup, user: ChatUser): Unit = {
    var channelToRemove: Option[Channel] = None
    chatRoomChannelGroup.forEach{ c =>
      Try(AttrHelper.getUsername(c)) match {
        case Failure(_) => channelToRemove
        case Success(_) => channelToRemove = Some(c)
      }
    }

    channelToRemove.map { c =>
      chatRoomChannelGroup.remove(c)
      c.close()
    }
  }

  protected def addUsernameToChanne(channel: Channel, user: ChatUser): Unit = {
    AttrHelper.setUsername(channel, user.username)
  }

}
