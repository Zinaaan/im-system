package com.zinan.im.tcp.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.zinan.im.codec.pack.LoginPack;
import com.zinan.im.codec.protocols.Message;
import com.zinan.im.common.constant.Constants;
import com.zinan.im.common.enums.ImConnectStatusEnum;
import com.zinan.im.common.enums.command.SystemCommand;
import com.zinan.im.common.model.UserClientDto;
import com.zinan.im.common.model.UserSession;
import com.zinan.im.tcp.redis.RedisManager;
import com.zinan.im.tcp.utils.SessionSocketHolder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import java.nio.ByteBuffer;

/**
 * @author lzn
 * @date 2023/06/21 16:28
 * @description Netty server for handling mutual communication and multi-ended login
 */
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<Message> {


    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("Client disconnected: " + ctx.channel().remoteAddress());
        log.info("Client disconnected: " + ctx.channel().pipeline());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, Message message) throws Exception {

        // Got request type
        Integer command = message.getMessageHeader().getCommand();

        // Log in
        if (SystemCommand.LOGIN.getCommand() == command) {

            // Set up the login user id
            LoginPack loginPack = JSON.parseObject(JSONObject.toJSONString(message.getMessagePack()), new TypeReference<LoginPack>() {
            }.getType());
            String userId = loginPack.getUserId();
            Integer appId = message.getMessageHeader().getAppId();
            Integer clientType = message.getMessageHeader().getClientType();
            context.channel().attr(AttributeKey.valueOf(Constants.USER_ID)).set(userId);
            context.channel().attr(AttributeKey.valueOf(Constants.APP_ID)).set(appId);
            context.channel().attr(AttributeKey.valueOf(Constants.CLIENT_TYPE)).set(clientType);

            // Store the current channel via Redis -> Hash table
            UserSession userSession = new UserSession();
            userSession.setAppId(appId);
            userSession.setUserId(userId);
            userSession.setClientType(clientType);
            userSession.setConnectionState(ImConnectStatusEnum.ONLINE_STATUS.getCode());
            RedissonClient redissonClient = RedisManager.getRedissonClient();
            // User session, format -> appId + USER_SESSION_CONSTANTS + userId
            RMap<String, String> map = redissonClient.getMap(appId + Constants.RedisConstants.USER_SESSION_CONSTANTS + userId);
            map.put(String.valueOf(message.getMessageHeader().getClientType()), JSONObject.toJSONString(userSession));

            // Store user login session locally
            UserClientDto userClientDto = new UserClientDto(userId, appId, clientType);
            SessionSocketHolder.put(userClientDto, (NioSocketChannel) context.channel());
        }
        // Log out
        else if (SystemCommand.LOGOUT.getCommand() == command) {
            // Remove local session and redis session
            SessionSocketHolder.removeUserSession((NioSocketChannel) context.channel());
        }
        // Heart beat
        else if (SystemCommand.PING.getCommand() == command) {
            context.channel().attr(AttributeKey.valueOf(Constants.READ_TIME)).set(System.currentTimeMillis());
        }

        System.out.println(message);

        final WriteListener listener = new WriteListener() {
            @Override
            public void messageRespond(boolean success) {
                System.out.println(success ? "reply success" : "reply fail");
            }
        };

        ByteBuf responseBuffer = Unpooled.copiedBuffer((message.toString()).getBytes());

        context.writeAndFlush(responseBuffer).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (listener != null) {
                    listener.messageRespond(future.isSuccess());
                }
            }

        });

        System.out.println("----------------------");
    }

    /**
     * {@link WriteListener} is the lister message status interface.
     */
    public interface WriteListener {
        void messageRespond(boolean success);
    }
}
