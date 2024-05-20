package com.ztc.netty.handlerchildhandler.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.Charset;
import java.util.Date;

/**
 * @ClassName FirstClientHandler
 * @Description TODO
 * @Author ztc
 * @Date 2024/5/15 14:44
 */
public class FirstClientHandler extends ChannelInboundHandlerAdapter {
    /**
     * @Author ztc
     * @Description 这个方法在客户端建立连接成功后会被调用
     * @Date 2024/5/15
     * @param ctx
     *
    **/
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        super.channelActive(ctx);
        System.out.println(new Date() + ": 客户端写出数据");
        //获取数据
        ByteBuf buffer = getBuffer(ctx);
        //写数据
        ctx.channel().writeAndFlush(buffer);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        System.out.println(new Date() + ":客户端读取到的数据 ->" + byteBuf.toString(Charset.forName("UTF-8")));
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
        byte[] bytes = "你好，闪电侠，我跟你学习 NETTY".getBytes(Charset.forName("UTF-8"));
        //填充数据到 ByteBuf
        buffer.writeBytes(bytes);
        return buffer;
    }
}
