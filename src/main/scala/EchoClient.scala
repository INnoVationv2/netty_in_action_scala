import io.netty.bootstrap.Bootstrap
import io.netty.buffer.{ByteBuf, Unpooled}
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.{ChannelHandlerContext, ChannelInitializer, SimpleChannelInboundHandler}
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.util.CharsetUtil

import java.net.InetSocketAddress
import java.util.Scanner

@Sharable
class EchoClientHandler extends SimpleChannelInboundHandler[ByteBuf] {
  override def channelActive(ctx: ChannelHandlerContext): Unit = {
    println("[Client]: Please Input String:")
    val line = scala.io.StdIn.readLine()
    ctx.writeAndFlush(Unpooled.copiedBuffer(line, CharsetUtil.UTF_8))
  }

  override def channelRead0(ctx: ChannelHandlerContext, in: ByteBuf): Unit = {
    println(s"Client received: " +
      s"${in.toString(CharsetUtil.UTF_8)}")
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    cause.printStackTrace()
    ctx.close()
  }
}

class EchoClient(host: String, port: Int) {
  def start(): Unit = {
    println(s"[Client] Work At $host:$port")
    val group = new NioEventLoopGroup()
    try {
      val b = new Bootstrap()
      b.group(group)
        .channel(classOf[NioSocketChannel])
        .remoteAddress(new InetSocketAddress(host, port))
        .handler(new ChannelInitializer[SocketChannel] {
          override def initChannel(ch: SocketChannel): Unit = {
            ch.pipeline().addLast(
              new EchoClientHandler()
            )
          }
        })
      val f = b.connect().sync()
      f.channel().closeFuture().sync()
    } finally {
      group.shutdownGracefully().sync()
    }
    println(s"[Client] Close")
  }
}