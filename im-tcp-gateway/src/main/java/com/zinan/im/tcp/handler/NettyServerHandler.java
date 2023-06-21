package com.zinan.im.tcp.handler;

import com.zinan.im.codec.protocols.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author lzn
 * @date 2023/06/21 16:28
 * @description
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<Message> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message message) throws Exception {
        System.out.println(message);
    }
}
