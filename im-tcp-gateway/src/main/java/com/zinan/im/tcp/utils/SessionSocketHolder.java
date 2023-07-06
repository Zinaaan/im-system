package com.zinan.im.tcp.utils;

import com.alibaba.fastjson.JSONObject;
import com.zinan.im.common.constant.Constants;
import com.zinan.im.common.enums.ImConnectStatusEnum;
import com.zinan.im.common.model.UserClientDto;
import com.zinan.im.common.model.UserSession;
import com.zinan.im.tcp.redis.RedisManager;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lzn
 * @date 2023/07/04 14:08
 * @description Session holder for the login users
 */
public class SessionSocketHolder {

    private static final Map<UserClientDto, NioSocketChannel> CHANNELS = new ConcurrentHashMap<>();

    public static void put(UserClientDto clientDto, NioSocketChannel socketChannel) {
        CHANNELS.put(clientDto, socketChannel);
    }

    public static NioSocketChannel get(UserClientDto clientDto) {
        return CHANNELS.get(clientDto);
    }

    public static boolean containsKey(UserClientDto clientDto) {
        return CHANNELS.containsKey(clientDto);
    }

    public static void removeByKey(UserClientDto clientDto) {
        CHANNELS.remove(clientDto);
    }

    public static void removeByValue(NioSocketChannel socketChannel) {
        CHANNELS.entrySet().stream().filter(channels -> channels.getValue() == socketChannel).forEach(channels -> CHANNELS.remove(channels.getKey()));
    }

    /**
     * Remove local session and redis session
     *
     * @param socketChannel The instance for NioSocketChannel
     */
    public static void removeUserSession(NioSocketChannel socketChannel) {
        // Remove local session
        String userId = (String) socketChannel.attr(AttributeKey.valueOf(Constants.USER_ID)).get();
        Integer appId = (Integer) socketChannel.attr(AttributeKey.valueOf(Constants.APP_ID)).get();
        Integer clientType = (Integer) socketChannel.attr(AttributeKey.valueOf(Constants.CLIENT_TYPE)).get();
        UserClientDto userClientDto = new UserClientDto(userId, appId, clientType);
        if (SessionSocketHolder.containsKey(userClientDto)) {
            SessionSocketHolder.removeByKey(userClientDto);
        }

        // Remove redis session
        RedissonClient redissonClient = RedisManager.getRedissonClient();
        RMap<String, String> map = redissonClient.getMap(appId + Constants.RedisConstants.USER_SESSION_CONSTANTS + userId);
        map.remove(String.valueOf(clientType));
        socketChannel.close();
    }

    /**
     * Remove local session and modify redis session status to "offline"
     *
     * @param socketChannel The instance for NioSocketChannel
     */
    public static void offlineUserSession(NioSocketChannel socketChannel) {
        // Remove local session
        String userId = (String) socketChannel.attr(AttributeKey.valueOf(Constants.USER_ID)).get();
        Integer appId = (Integer) socketChannel.attr(AttributeKey.valueOf(Constants.APP_ID)).get();
        Integer clientType = (Integer) socketChannel.attr(AttributeKey.valueOf(Constants.CLIENT_TYPE)).get();
        UserClientDto userClientDto = new UserClientDto(userId, appId, clientType);
        if (SessionSocketHolder.containsKey(userClientDto)) {
            SessionSocketHolder.removeByKey(userClientDto);
        }

        // Modify the redis session status to "offline"
        RedissonClient redissonClient = RedisManager.getRedissonClient();
        RMap<String, String> map = redissonClient.getMap(appId + Constants.RedisConstants.USER_SESSION_CONSTANTS + userId);
        String session = map.get(clientType.toString());
        if (!StringUtils.isBlank(session)) {
            UserSession userSession = JSONObject.parseObject(session, UserSession.class);
            userSession.setConnectionState(ImConnectStatusEnum.OFFLINE_STATUS.getCode());
            map.put(clientType.toString(), JSONObject.toJSONString(userSession));
        }
        socketChannel.close();
    }
}
