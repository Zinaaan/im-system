package com.zinan.im.tcp.handler;

import com.zinan.im.common.constant.Constants;
import com.zinan.im.tcp.utils.SessionSocketHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lzn
 * @date 2023/07/05 21:23
 * @description Netty server for handling heart beat events
 * e.g. If someone logged out from the system to the background(not actually log out), this handler will handle this events
 */
@Slf4j
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {

    private final Long heartBeatTime;

    public HeartBeatHandler(Long heartBeatTime) {
        this.heartBeatTime = heartBeatTime;
    }

    /**
     * If read and write timeout events(IdleState.ALL_IDLE) of IdleStateHandler is triggered, the socket channel will invoke this method
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        // Check if the event is IdleStateEvent (For event triggered, e.g. read idle/write idle/all idle)
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                log.info("The read is idled");
            } else if (event.state() == IdleState.WRITER_IDLE) {
                log.info("The write is idled");
            } else if (event.state() == IdleState.ALL_IDLE) {
                Long lastReadTime = (Long) ctx.channel().attr(AttributeKey.valueOf(Constants.READ_TIME)).get();
                long now = System.currentTimeMillis();

                if (lastReadTime != null && now - lastReadTime > heartBeatTime) {
                    // Switch the user status to "Offline"(Log out to the background)
                    SessionSocketHolder.offlineUserByChannel((NioSocketChannel) ctx.channel());
                }
            }
        }
    }
}
