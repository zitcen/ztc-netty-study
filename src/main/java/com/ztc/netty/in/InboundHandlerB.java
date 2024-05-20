package com.ztc.netty.in;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @ClassName InboundHandlerB
 * @Description TODO
 * @Author ztc
 * @Date 2024/5/15 15:48
 */
public class InboundHandlerB extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("InBoundHandlerB: " + msg);
        super.channelRead(ctx, msg);
    }
}
