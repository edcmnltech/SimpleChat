package main.scala.com.simplechat.netty.handler

import java.io.{File, RandomAccessFile}

import io.netty.channel._
import io.netty.handler.codec.http._
import io.netty.handler.ssl.SslHandler
import io.netty.handler.stream.ChunkedNioFile


class HttpRequestHandler(wsUri: String) extends SimpleChannelInboundHandler[FullHttpRequest] {

  override def channelRead0(ctx: ChannelHandlerContext, request: FullHttpRequest): Unit = {

    if (wsUri.equalsIgnoreCase(request.uri())) {
      ctx.fireChannelRead(request.retain())
    } else {
      if (HttpHeaders.is100ContinueExpected(request)) send100Continue(ctx)

      val index: File = new File("sample.txt")

      val file = new RandomAccessFile(index, "r")
      val response = new DefaultHttpResponse(request.protocolVersion(), HttpResponseStatus.OK)
      response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8")

      val keepAlive = HttpHeaders.isKeepAlive(request)
      if (keepAlive)
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, file.length())
        response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE)

      ctx.write(response)

      if (ctx.pipeline().get(classOf[SslHandler]) == null)
        ctx.write(new DefaultFileRegion(file.getChannel, 0, file.length()))
      else
        ctx.write(new ChunkedNioFile(file.getChannel))

      val future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT)
      if (!keepAlive) future.addListener(ChannelFutureListener.CLOSE)
    }
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    throw new Exception {
      cause.printStackTrace()
      ctx.close()
    }
  }

  def send100Continue(ctx: ChannelHandlerContext): ChannelFuture = {
    val response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE)
    ctx.writeAndFlush(response)
  }

}
