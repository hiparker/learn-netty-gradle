package com.parker.netty.study.s01;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ReferenceCountUtil;

/**
 * @BelongsProject: learn-netty-gradle
 * @BelongsPackage: com.parker.netty.study01
 * @Author: Parker
 * @CreateTime: 2020-08-18 21:01
 * @Description: 客户端
 */
public class Client {

    public static void main(String[] args) {

        EventLoopGroup group = new NioEventLoopGroup(1);

        Bootstrap b = new Bootstrap();
        try{
            ChannelFuture f = b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            System.out.println("初始化");
                            ch.pipeline().addLast(new ClientChanHandler());
                        }
                    })
                    .connect("localhost",12345)
                    .sync();

            if(f.isSuccess()){
                System.out.println("客户端启动～");
            }
            f.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 关闭线程组
            group.shutdownGracefully();
        }


    }

}

class ClientChanHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        super.channelWritabilityChanged(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        // channel 激活时 向服务端发送信息
        ByteBuf buffer = Unpooled.copiedBuffer("hello".getBytes());
        ctx.writeAndFlush(buffer);

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = null;
        try {
            buf = (ByteBuf) msg;

            int i = buf.readableBytes();
            byte[] bytes = new byte[i];
            buf.getBytes(buf.readerIndex(),bytes);
            System.out.println(new String(bytes));

        }finally {
            if(buf != null ) ReferenceCountUtil.release(buf);
            System.out.println(buf);
            System.out.println(buf.refCnt());
        }
    }
}
