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
            // Remove local session
            String userId = (String) context.channel().attr(AttributeKey.valueOf(Constants.USER_ID)).get();
            Integer appId = (Integer) context.channel().attr(AttributeKey.valueOf(Constants.APP_ID)).get();
            Integer clientType = (Integer) context.channel().attr(AttributeKey.valueOf(Constants.CLIENT_TYPE)).get();
            UserClientDto userClientDto = new UserClientDto(userId, appId, clientType);
            if (SessionSocketHolder.containsKey(userClientDto)) {
                SessionSocketHolder.removeByKey(userClientDto);
            }

            // Remove redis session
            RedissonClient redissonClient = RedisManager.getRedissonClient();
            RMap<String, String> map = redissonClient.getMap(appId + Constants.RedisConstants.USER_SESSION_CONSTANTS + userId);
            map.remove(String.valueOf(clientType));
            context.channel().close();
        }

        System.out.println(message);
    }
}
