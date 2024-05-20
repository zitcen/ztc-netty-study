package com.ztc.netty.out;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

/**
 * @ClassName OutboundHandlerA
 * @Description TODO
 * @Author ztc
 * @Date 2024/5/15 15:46
 */
public class OutboundHandlerA extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        System.out.println("OutBoundHandlerA: " + msg);
        super.write(ctx, msg, promise);

    }
}
