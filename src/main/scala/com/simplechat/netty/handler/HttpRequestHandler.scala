package com.simplechat.netty.handler

import java.io.{File, RandomAccessFile}

import akka.actor.{ActorContext, ActorRef, Props}
import com.simplechat.actor.UserActor
import com.simplechat.adapter.ChatRooms
import com.simplechat.netty.helper.{AttrHelper, UrlHelper}
import com.simplechat.repository._
import io.netty.channel._
import io.netty.channel.group.ChannelGroup
import io.netty.handler.codec.http._
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler
import io.netty.handler.ssl.SslHandler
import io.netty.handler.stream.ChunkedNioFile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class HttpRequestHandler(wsUri: String, actorContext: ActorContext) extends SimpleChannelInboundHandler[FullHttpRequest]
  with MySqlRepository
  with ChatUserRepository
  with ChatRoomRepository {

  override def channelRead0(ctx: ChannelHandlerContext, request: FullHttpRequest): Unit = {

    val requestUri = request.uri()

    if (requestUri.contains(UrlHelper.WS_PATH)) {
      if (requestUri.contains(UrlHelper.USER_PATH)) {
        if (requestUri.contains(UrlHelper.ROOM_PATH)) {

          implicit val nec: ExecutionContext = ExecutionContext.fromExecutor(ctx.executor())
          request.retain()

          val futureChatRoomActorRef: Future[(ActorRef, Props, ChatUsername)] = for {
            validChatRoom <- extractChatRoom(ctx, request)
            validChatUser <- extractChatUser(ctx, request)
            userActorProps = UserActor.props(validChatUser.username)
            chatRoomActorRef <- ChatRooms.actorRefFor(actorContext, validChatRoom.name)
          } yield (chatRoomActorRef, userActorProps, validChatUser.username)

          futureChatRoomActorRef.onComplete {
            case Success((chatRoomActorRef, userActorProps, username)) =>
              ctx.pipeline().addLast(new WebSocketServerProtocolHandler(request.uri()))
              ctx.pipeline().addLast(new ChatWebSocketFrameHandler(chatRoomActorRef, userActorProps, username, actorContext))
              ctx.fireChannelRead(request)
            case Failure(exception) =>
              //possible hook for returning the error
              println(s"failed to create chatroom caused by: ${exception.getMessage}")
              Future(ctx.close())
          }
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

  protected def extractChatUser(ctx: ChannelHandlerContext, request: FullHttpRequest): Future[ChatUser] = {
    val chatUser = UrlHelper.getChatUsername(request.uri())
    selectByUsername(chatUser)
  }

  protected def extractChatRoom(ctx: ChannelHandlerContext, request: FullHttpRequest): Future[ChatRoom] = {
    val chatRoom = UrlHelper.getChatRoomName(request.uri())
    selectByRoomName(chatRoom)
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
