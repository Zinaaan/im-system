package com.zinan.im.tcp.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.zinan.im.codec.pack.LoginPack;
import com.zinan.im.codec.protocols.Message;
import com.zinan.im.common.constant.Constants;
import com.zinan.im.common.enums.ImConnectStatusEnum;
import com.zinan.im.common.enums.command.SystemCommand;
import com.zinan.im.common.model.UserSession;
import com.zinan.im.tcp.redis.RedisManager;
import com.zinan.im.tcp.utils.SessionSocketHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

/**
 * @author lzn
 * @date 2023/06/21 16:28
 * @description Netty server for handling mutual communication and multi-ended login
 */
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<Message> {


    @Override
    protected void channelRead0(ChannelHandlerContext context, Message message) throws Exception {

        // Got request type
        Integer command = message.getMessageHeader().getCommand();

        if (SystemCommand.LOGIN.getCommand() == command) {

            // Set up the login user id
            LoginPack loginPack = JSON.parseObject(JSONObject.toJSONString(message.getMessagePack()), new TypeReference<LoginPack>() {
            }.getType());
            String userId = loginPack.getUserId();
            context.channel().attr(AttributeKey.valueOf("userId")).set(userId);

            // Store the current channel via Redis -> Hash table
            UserSession userSession = new UserSession();
            userSession.setAppId(message.getMessageHeader().getAppId());
            userSession.setUserId(userId);
            userSession.setClientType(message.getMessageHeader().getClientType());
            userSession.setConnectionState(ImConnectStatusEnum.ONLINE_STATUS.getCode());
            RedissonClient redissonClient = RedisManager.getRedissonClient();
            // User session, format -> appId + USER_SESSION_CONSTANTS + userId
            RMap<String, String> map = redissonClient.getMap(message.getMessageHeader().getAppId() + Constants.RedisConstants.USER_SESSION_CONSTANTS + userId);
            map.put(String.valueOf(message.getMessageHeader().getClientType()), JSONObject.toJSONString(userSession));

            SessionSocketHolder.put(userId, (NioSocketChannel) context.channel());
        }

        System.out.println(message);
    }
}
