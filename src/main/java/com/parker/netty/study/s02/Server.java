package com.parker.netty.study.s02;

import com.alibaba.fastjson.JSONObject;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @BelongsProject: learn-netty-gradle
 * @BelongsPackage: com.parker.netty.study01
 * @Author: Parker
 * @CreateTime: 2020-08-18 21:10
 * @Description: 服务端
 */
public class Server {

    public static void main(String[] args) {

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        ServerBootstrap b = new ServerBootstrap();

        b.group(bossGroup,workGroup);
        b.channel(NioServerSocketChannel.class);
        b.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                System.out.println("服务端初始化");
                ch.pipeline().addLast(new ServerChannelHandler());
            }
        });

        try{
            ChannelFuture f = b.bind(12345).sync();
            if(f.isSuccess()){
                System.out.println("服务端启动！！！");
            }

            f.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}

class ServerChannelHandler extends ChannelInboundHandlerAdapter{

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg == null){
            return;
        }

        long time = System.currentTimeMillis();

        ByteBuf buf = null;
        buf = (ByteBuf) msg;

        int i = buf.readableBytes();
        byte[] bytes = new byte[i];
        buf.getBytes(buf.readerIndex(),bytes);

        // 处理消息
        JSONObject jb = (JSONObject) JSONObject.parse(new String(bytes));
        StringBuffer stb = new StringBuffer();
        stb.append(jb.get("name"))
                .append("(")
                .append(DateUtil.formatDate(DateUtil.long2Date(time)))
                .append(")\n")
                .append(jb.get("content"));

        buf = Unpooled.copiedBuffer(stb.toString().getBytes());

        // 服务器会写聊天室所有信息
        BroadCaster.INSTANCE.cast(buf);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        BroadCaster.INSTANCE.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端关闭");
        BroadCaster.INSTANCE.remove(ctx.channel());
        ctx.close();
    }
}
