package com.ztc.netty.demo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;

import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.AttributeKey;



/**
 * @ClassName NettySimpleServer
 * @Description TODO
 * @Author ztc
 * @Date 2024/5/15 10:25
 */
public class NettySimpleServer {
    public static void main(String[] args) {
        // 启动一个netty服务端需要指定 线程模型 IO模型 业务处理逻辑

        //监听端口，接收新连接的线程组
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        //处理每一个连接的数据读写的线程组
        NioEventLoopGroup bossWork = new NioEventLoopGroup();
        //引导服务端的启动工作
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, bossWork)
                .channel(NioServerSocketChannel.class)//指定 IO 模型
                .childHandler(new ChannelInitializer<NioSocketChannel>() {//定义后续每个连接的数据读写
                    @Override
                    protected void initChannel(NioSocketChannel ch) {
                        ch.pipeline().addLast(new StringDecoder()).addLast(new SimpleChannelInboundHandler<String>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                                System.out.println(msg);
                            }
                        });
                    }
                });
        //handle() 方法，用于服务器启动的时候添加逻辑；注意 childHandler() 方法是为每一个新的连接添加逻辑
        serverBootstrap.handler(new ChannelInitializer<NioServerSocketChannel>() {
            @Override
            protected void initChannel(NioServerSocketChannel ch) throws Exception {

            }
        });

        //attr() 方法，给NioServerSocketChannel 维护一个map
        serverBootstrap.attr(AttributeKey.newInstance("serverName"), "nettyServer");
        // childAttr() 方法 可以为每个连接都指定自定义的属性
        serverBootstrap.childAttr(AttributeKey.newInstance("clientKey"), "clientValue");
        // option() 方法 用于给服务端Channel设置一些TCP参数 SO_BACKLOG 表示系统用于临时存放已完成三次握手的请求的队列的最大长度
        serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        // childOption() 方法 用于给每个连接都设置一些TCP参数，SO_KEEPALIVE 表示是否开启TCP心跳机制
        serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        // TCP_NODELAY 表示是否开启Nagle算法
        serverBootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        bind(serverBootstrap, 8000);
    }

    public static void bind(final ServerBootstrap bootstrap, final int port) {
        bootstrap.bind(port).addListener((future) -> {
                    if (future.isSuccess()) {
                        System.out.println(port + ":端口绑定成功！");
                    } else {
                        System.out.println(port +":端口绑定失败");
                        bind(bootstrap, port + 1);
                    }
                }
        );
    }
}
