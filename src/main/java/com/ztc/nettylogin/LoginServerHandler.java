package com.ztc.nettylogin;

import com.ztc.message.MessageRequestPacket;
import com.ztc.message.MessageResponsePacket;
import com.ztc.packetJava.LoginRequestPacket;
import com.ztc.packetJava.LoginResponsePacket;
import com.ztc.packetJava.Packet;
import com.ztc.packetJava.PacketCodeC;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Date;

/**
 * @ClassName LoginServerHandler
 * @Description TODO
 * @Author ztc
 * @Date 2024/7/19 11:43
 */
public class LoginServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        //获取 ByteBuf 对象
        ByteBuf byteBuf = (ByteBuf) msg;
//        System.out.println(byteBuf.toString());
        //解码
        Packet packet = PacketCodeC.INSTANCE.decode(byteBuf);
        //判断是否是登录请求包
        if (packet instanceof LoginRequestPacket) {
            //响应数据
            LoginResponsePacket responsePacket = new LoginResponsePacket();
            responsePacket.setVersion(packet.getVersion());
            //校验数据
            LoginRequestPacket requestPacket = (LoginRequestPacket) packet;
            responsePacket.setUserId(requestPacket.getUserId());
            responsePacket.setUserName(requestPacket.getUserName());
            if (valid(requestPacket)) {
                //校验成功
                responsePacket.setSuccess(true);
                System.out.println("校验成功");
            } else {
                //校验失败
                responsePacket.setSuccess(false);
                responsePacket.setReason("账号密码校验失败");
            }
            ByteBuf responseByteBuf = PacketCodeC.INSTANCE.encode(ctx.alloc(), responsePacket);
            ctx.channel().writeAndFlush(responseByteBuf);
        }else if(packet instanceof MessageRequestPacket){
            MessageRequestPacket messageRequestPacket = (MessageRequestPacket) packet;
            System.out.println(new Date() + ": 收到客户端的消息："+messageRequestPacket.getMsg());
            MessageResponsePacket messageResponsePacket = new MessageResponsePacket();
            messageResponsePacket.setMsg("服务端回复【" + messageRequestPacket.getMsg() +"】");
            ByteBuf buf = PacketCodeC.INSTANCE.encode(ctx.alloc(), messageResponsePacket);
            ctx.channel().writeAndFlush(buf);
        }else{
            //校验失败
        }

    }
    private boolean valid(LoginRequestPacket packet){
        return true;
    }
}
