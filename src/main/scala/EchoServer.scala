import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel

import java.net.InetSocketAddress
import io.netty.buffer.{ByteBuf, Unpooled}
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.{ChannelFutureListener, ChannelHandlerContext, ChannelInboundHandler, ChannelInboundHandlerAdapter}
import io.netty.util.CharsetUtil

@Sharable
class EchoServerHandler extends ChannelInboundHandlerAdapter{
  override def channelRead(ctx: ChannelHandlerContext, msg: Any): Unit = {
    val in = msg.asInstanceOf[ByteBuf]
    println(s"Server received: ${in.toString(CharsetUtil.UTF_8)}")
    ctx.write(in)
  }

  override def channelReadComplete(ctx: ChannelHandlerContext): Unit = {
    ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
      .addListener(ChannelFutureListener.CLOSE)
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    cause.printStackTrace()
    ctx.close()
  }
}

class EchoServer(port: Int) {
  def start(): Unit = {
    println(s"Echo Server Listen At $port")
    val serverHandler = new EchoServerHandler()
    val group = new NioEventLoopGroup()
    try{
      val b = new ServerBootstrap()
      b.group(group)
        .channel(classOf[NioServerSocketChannel])
        .localAddress(new InetSocketAddress(port))
        .childHandler(new ChannelInitializer[SocketChannel] {
          override def initChannel(ch: SocketChannel): Unit = {
              ch.pipeline().addLast(serverHandler)
          }
        })
      val f = b.bind().sync()
      f.channel().closeFuture().sync()
    } finally {
      group.shutdownGracefully().sync()
    }
    println(s"Echo Server Close")
  }
}
