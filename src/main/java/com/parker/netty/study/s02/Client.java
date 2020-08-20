package com.parker.netty.study.s02;

import com.alibaba.fastjson.JSONObject;
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
public enum Client {

    INSTANCE;

    public Channel  channel = null;

    public void connect(ClientFrame cf){
        EventLoopGroup group = new NioEventLoopGroup(1);

        Bootstrap b = new Bootstrap();
        try{
            ChannelFuture channelFuture = b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            System.out.println("初始化");
                            ch.pipeline().addLast(new ClientChanHandler(cf));
                        }
                    })
                    .connect("localhost",12345)
                    .sync();

            if(channelFuture.isSuccess()){
                channel = channelFuture.channel();
                System.out.println("客户端启动！");
            }
            channelFuture.channel().closeFuture().sync();

        } catch (Exception e) {
            String errorStr = "连接服务器失败";
            cf.print("连接服务器失败");
            System.out.println(errorStr);
        } finally {
            // 关闭线程组
            group.shutdownGracefully();
        }
    }

    public Channel getChannel() {
        return this.channel;
    }

    public void send(String name,String msg){
        if(msg == null){
            return;
        }
        JSONObject jb = new JSONObject();
        jb.put("name",name);
        jb.put("content",msg);

        // 向服务器发送信息
        ByteBuf buf = Unpooled.copiedBuffer(jb.toString().getBytes());
        if(channel != null){
            channel.writeAndFlush(buf);
        }
    }
}

class ClientChanHandler extends ChannelInboundHandlerAdapter {

    private ClientFrame cf;

    public ClientChanHandler(ClientFrame cf){
        this.cf = cf;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        /*// channel 激活时 向服务端发送信息
        ByteBuf buffer = Unpooled.copiedBuffer("hello".getBytes());
        ctx.writeAndFlush(buffer);*/

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg == null){
            return;
        }

        ByteBuf buf = null;
        try {
            buf = (ByteBuf) msg;

            int i = buf.readableBytes();
            byte[] bytes = new byte[i];
            buf.getBytes(buf.readerIndex(),bytes);

            cf.print(new String(bytes));
        }finally {
            if(buf != null ) ReferenceCountUtil.release(buf);
        }
    }
}
