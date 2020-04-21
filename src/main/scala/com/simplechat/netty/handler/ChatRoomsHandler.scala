package com.simplechat.netty.handler

import com.fasterxml.jackson.databind.ObjectMapper
import io.netty.buffer.Unpooled
import io.netty.channel.{ChannelFutureListener, ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.handler.codec.http.{HttpHeaders, _}
import io.netty.util.CharsetUtil

class ChatRoomsHandler extends SimpleChannelInboundHandler[FullHttpRequest] {

  override def channelRead0(ctx: ChannelHandlerContext, request: FullHttpRequest): Unit = {


    val TYPE_PLAIN: CharSequence = HttpHeaders.newEntity("text/plain; charset=UTF-8")
    val TYPE_JSON: CharSequence = HttpHeaders.newEntity("application/json; charset=UTF-8")
    val SERVER_NAME: CharSequence = HttpHeaders.newEntity("Netty")
    val CONTENT_TYPE_ENTITY: CharSequence = HttpHeaders.newEntity(HttpHeaders.Names.CONTENT_TYPE)
    val DATE_ENTITY: CharSequence = HttpHeaders.newEntity(HttpHeaders.Names.DATE)
    val CONTENT_LENGTH_ENTITY: CharSequence = HttpHeaders.newEntity(HttpHeaders.Names.CONTENT_LENGTH)
    val SERVER_ENTITY: CharSequence = HttpHeaders.newEntity(HttpHeaders.Names.SERVER)
    val MAPPER: ObjectMapper = null


    if (request.uri().contains("/rooms")) {
      val buff = Unpooled.unreleasableBuffer(Unpooled.directBuffer.writeBytes("Hello, World!".getBytes(CharsetUtil.UTF_8)))
      val contentType = TYPE_JSON
      val response: FullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buff, false)

      val headers = response.headers
      headers.set(CONTENT_TYPE_ENTITY, contentType)
      headers.set(SERVER_ENTITY, SERVER_NAME)
      headers.set(DATE_ENTITY, DATE_ENTITY)
      headers.set(CONTENT_LENGTH_ENTITY, CONTENT_LENGTH_ENTITY)

      ctx.write(response).addListener(ChannelFutureListener.CLOSE)
    }

  }

}
