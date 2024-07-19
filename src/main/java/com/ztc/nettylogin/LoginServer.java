package com.ztc.nettylogin;

import com.ztc.netty.handlerchildhandler.server.FirstServerHandler;
import com.ztc.netty.in.InboundHandlerA;
import com.ztc.netty.in.InboundHandlerB;
import com.ztc.netty.out.OutboundHandlerA;
import com.ztc.netty.out.OutboundHandlerB;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @ClassName LoginService
 * @Description TODO
 * @Author ztc
 * @Date 2024/7/19 10:22
 */
public class LoginServer {
    public static void main(String[] args) {
        ServerBootstrap bootstrap = new ServerBootstrap();
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        //处理读数据的逻辑
//                        ch.pipeline().addLast(new InboundHandlerA()).addLast(new InboundHandlerB());
                        ch.pipeline().addLast(new LoginServerHandler());
                        //处理写数据的逻辑
//                        ch.pipeline().addLast(new OutboundHandlerA()).addLast(new OutboundHandlerB());
                    }
                });
        bootstrap.bind("127.0.0.1",8000);
    }
}
