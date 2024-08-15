package com.ztc.netty.handlerchildhandler.server;

import com.ztc.netty.in.InboundHandlerA;
import com.ztc.netty.in.InboundHandlerB;
import com.ztc.netty.in.InboundHandlerC;
import com.ztc.netty.out.OutboundHandlerA;
import com.ztc.netty.out.OutboundHandlerB;
import com.ztc.netty.out.OutboundHandlerC;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @ClassName CommunicationNettyServer
 * @Description 客户端向服务端通信
 * @Author ztc
 * @Date 2024/5/15 15:36
 */
public class CommunicationNettyServer {
    public static void main(String[] args) {
        ServerBootstrap bootstrap = new ServerBootstrap();
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        bootstrap.group(boss, worker).channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        //处理读数据的逻辑
                        ch.pipeline().addLast(new InboundHandlerA()).addLast(new InboundHandlerB()).addLast(new InboundHandlerC());
                        ch.pipeline().addLast(new FirstServerHandler());
                        //处理写数据的逻辑
                        ch.pipeline().addLast(new OutboundHandlerA()).addLast(new OutboundHandlerB()).addLast(new OutboundHandlerC());
                    }
                });
        bootstrap.bind("127.0.0.1",8000);
    }
}
