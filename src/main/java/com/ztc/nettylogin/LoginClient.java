package com.ztc.nettylogin;

import com.ztc.netty.handlerchildhandler.client.FirstClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @ClassName LoginClient
 * @Description TODO
 * @Author ztc
 * @Date 2024/7/19 10:22
 */
public class LoginClient {
    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup();
        bootstrap.group(group).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LoginClientHandler());
                    }
                });
        bootstrap.connect("127.0.0.1",8000);
    }
}
