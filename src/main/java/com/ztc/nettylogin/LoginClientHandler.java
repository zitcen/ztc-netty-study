package com.ztc.nettylogin;

import com.ztc.packetJava.LoginRequestPacket;
import com.ztc.packetJava.LoginResponsePacket;
import com.ztc.packetJava.Packet;
import com.ztc.packetJava.PacketCodeC;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Date;
import java.util.UUID;

/**
 * @ClassName ClientHandler
 * @Description TODO
 * @Author ztc
 * @Date 2024/7/19 11:42
 */
public class LoginClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //创建登录对象
        System.out.println(new Date() + ": 客户端开始登录");
        //创建登录对象
        LoginRequestPacket loginRequestPacket = new LoginRequestPacket();

        loginRequestPacket.setUserId(UUID.randomUUID().toString());
        loginRequestPacket.setPassword("pwd");
        loginRequestPacket.setUserName("yxy");
        //编码
        ByteBuf buffer = PacketCodeC.INSTANCE.encode(ctx.alloc(),loginRequestPacket);
        //写数据
        ctx.channel().writeAndFlush(buffer);

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //获取 ByteBuf 对象
        ByteBuf byteBuf = (ByteBuf) msg;
        //解包
        Packet packet = PacketCodeC.INSTANCE.decode(byteBuf);

        if(packet instanceof LoginResponsePacket){
            LoginResponsePacket responsePacket = (LoginResponsePacket) packet;
            if(responsePacket.isSuccess()){
                System.out.println(new Date() + "：客户端登录成功");
            }else{
                System.out.println(new Date() + ":客户端登录失败");
            }
        }else{

        }


    }
}
