package com.simplechat.netty.handler

import java.io.{File, RandomAccessFile}

import akka.actor.ActorContext
import com.simplechat.actor.UserActor
import com.simplechat.adapter.ChatRooms
import com.simplechat.netty.{AttrHelper, UrlHelper}
import com.simplechat.repository._
import io.netty.channel._
import io.netty.channel.group.ChannelGroup
import io.netty.handler.codec.http._
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler
import io.netty.handler.ssl.SslHandler
import io.netty.handler.stream.ChunkedNioFile

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Success, Try}

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

          val userActorProps = UserActor.props(validChatUser.username)
          val chatRoomActorRef = ChatRooms.actorRefFor(validChatRoom.name)

          ctx.pipeline().addLast(new WebSocketServerProtocolHandler(request.uri()))
          ctx.pipeline().addLast(new ChatWebSocketFrameHandler(chatRoomActorRef, userActorProps, validChatUser.username, actorContext))
          ctx.fireChannelRead(request.retain())
        } else {
          println("no chat room found in url.")
          ctx.close()
        }
      } else {
        println("no user found in url.")
        ctx.close()
      }
    } else if(request.uri().equalsIgnoreCase(UrlHelper.CHAT_ROOM_PATH)) {
      loadChatRoomPage(ctx, request)
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

  override def channelUnregistered(ctx: ChannelHandlerContext): Unit = super.channelUnregistered(ctx)

  protected def extractChatUser(ctx: ChannelHandlerContext, request: FullHttpRequest): ChatUser = {
    val chatUser = UrlHelper.getChatUsername(request.uri())
    Await.result(selectByUsername(chatUser), 2.seconds)
  }

  protected def extractChatRoom(ctx: ChannelHandlerContext, request: FullHttpRequest): ChatRoom = {
    val chatRoom = UrlHelper.getChatRoomName(request.uri())
    Await.result(selectByRoomName(chatRoom), 2.seconds)
  }

  protected def duplicateChannelCheck(chatRoomChannelGroup: ChannelGroup, user: ChatUser, actorContext: ActorContext): Unit = {
    chatRoomChannelGroup.forEach{ c =>
      Try(AttrHelper.getUsername(c) == user.username) match {
        case Success(true) =>
          println(s"there exist a dup user ${AttrHelper.getUsername(c) == user.username}")
          val futureClose = c.close()
          futureClose.addListener((f: ChannelFuture) => {
            if (f.isDone) chatRoomChannelGroup.remove(c)
          })
        case _ => //nothing to do
      }
    }
  }

  protected def loadChatRoomPage(ctx: ChannelHandlerContext, request: FullHttpRequest): Unit = {
    val url = getClass.getResource(UrlHelper.CHAT_ROOM_PAGE_PATH)
    val filePath = new File(url.getPath)
    val file = new RandomAccessFile(filePath, "r")
    val response = new DefaultHttpResponse(request.protocolVersion(), HttpResponseStatus.OK)
    response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, file.length())
    val keepAlive = HttpHeaders.isKeepAlive(request)
    if (keepAlive) {
      response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE)
    }
    ctx.write(response)
    if (ctx.pipeline().get(classOf[SslHandler]) == null) {
      ctx.write(new DefaultFileRegion(file.getChannel, 0, file.length()))
    } else {
      ctx.write(new ChunkedNioFile(file.getChannel))
    }
    val future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT)
    if (!keepAlive) {
      future.addListener(ChannelFutureListener.CLOSE)
    }
  }


}
