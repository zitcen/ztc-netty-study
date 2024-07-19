package com.ztc.netty.handlerchildhandler.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.Buffer;
import java.nio.charset.Charset;
import java.util.Date;

/**
 * @ClassName FirstServerHandler
 * @Description TODO
 * @Author ztc
 * @Date 2024/5/15 14:58
 */
public class FirstServerHandler extends ChannelInboundHandlerAdapter {
    //这个方法里的逻辑和客户端侧类似，获取服务端侧关于这个连接的逻辑处理链Pipeline，
    // 然后添加一个逻辑处理器，负责读取客户端发来的数据。
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        super.channelRead(ctx, msg);
        //读取客户端的数据
        ByteBuf byteBuf = (ByteBuf) msg;
        System.out.println(new Date() + ":服务端读取到的数据 ->" + byteBuf.toString(Charset.forName("UTF-8")));

        // 返回数据到客户端
        System.out.println(new Date() + "：服务端写出数据");
        //获取数据
        ByteBuf buffer = getBuffer(ctx);
        //写数据
        ctx.channel().writeAndFlush(buffer);
    }

    /**
     * @Author ztc
     * @Description 获取二进制 抽象ByteBuf的实体对象
     * @Date 2024/5/15
     * @param ctx
     * @return io.netty.buffer.ByteBuf
     *
     **/
    private ByteBuf getBuffer(ChannelHandlerContext ctx){
        //获取二进制抽象 ByteBuf
        ByteBuf buffer = ctx.alloc().buffer();
        //准备数据，指定字符串的字符集为 UTF-8
        byte[] bytes = "你好，先生，欢迎来学习 NETTY".getBytes(Charset.forName("UTF-8"));
        //填充数据到 ByteBuf
        buffer.writeBytes(bytes);
        return buffer;
    }
}
