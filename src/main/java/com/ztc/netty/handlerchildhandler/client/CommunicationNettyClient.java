package com.ztc.netty.handlerchildhandler.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @ClassName CommunicationNettyServer
 * @Description 客户端向服务端通信
 * @Author ztc
 * @Date 2024/5/15 15:36
 */
public class CommunicationNettyClient {
    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup();
        bootstrap.group(group).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new FirstClientHandler());
                    }
                });
        bootstrap.connect("127.0.0.1",8000);
    }
}
