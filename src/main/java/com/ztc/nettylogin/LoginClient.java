package com.ztc.nettylogin;

import com.ztc.message.MessageRequestPacket;
import com.ztc.netty.handlerchildhandler.client.FirstClientHandler;
import com.ztc.packetJava.PacketCodeC;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Scanner;

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
//        bootstrap.connect("127.0.0.1",8000);
        connect(bootstrap,"127.0.0.1",8000,0);
    }

    private static void connect(Bootstrap bootstrap,String host,int port,int retry){
        bootstrap.connect(host,port).addListener(future -> {
            if(future.isSuccess()){
                Channel channel = ((ChannelFuture) future).channel();
                startConsoleThread(channel);
            }
        });
    }
    private static void startConsoleThread(Channel channel){
        new Thread(()->{
            while (!Thread.interrupted()){
                System.out.println("输入消息到服务端：");
                Scanner scanner = new Scanner(System.in);
                String line = scanner.nextLine();
                MessageRequestPacket requestPacket = new MessageRequestPacket();
                requestPacket.setMsg(line);
                ByteBuf byteBuf = PacketCodeC.INSTANCE.encode(channel.alloc(), requestPacket);
                channel.writeAndFlush(byteBuf);
            }
        }).start();
    }
}
