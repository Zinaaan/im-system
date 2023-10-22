package com.zinan.im.tcp.utils;

import com.alibaba.fastjson.JSONObject;
import com.zinan.im.common.constant.Constants;
import com.zinan.im.common.enums.ImConnectStatusEnum;
import com.zinan.im.common.model.UserClientDto;
import com.zinan.im.common.model.UserSession;
import com.zinan.im.tcp.redis.RedisSessionOperator;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RMap;

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
    public static void removeUserByChannel(NioSocketChannel socketChannel) {
        // Remove local session
        UserClientDto client = createClient(socketChannel);
        removeUserByUnique(client);
        socketChannel.close();
    }

    public static void removeUserByUnique(UserClientDto client) {
        // Remove local session
        if (SessionSocketHolder.containsKey(client)) {
            SessionSocketHolder.removeByKey(client);
        }

        // Remove redis session
        RedisSessionOperator.getInstance().removeSessionByClient(client);
    }

    /**
     * Remove local session and modify redis session status to "offline"
     *
     * @param socketChannel The instance for NioSocketChannel
     */
    public static void offlineUserByChannel(NioSocketChannel socketChannel) {
        // Remove local session
        UserClientDto client = createClient(socketChannel);
        offlineUserByUnique(client);
        socketChannel.close();
    }

    public static void offlineUserByUnique(UserClientDto client) {
        if (SessionSocketHolder.containsKey(client)) {
            SessionSocketHolder.removeByKey(client);
        }

        // Modify the redis session status to "offline"
        RMap<Integer, String> map = RedisSessionOperator.getInstance().getSessionByClient(client);
        String session = map.get(client.getClientType());
        if (!StringUtils.isBlank(session)) {
            UserSession userSession = JSONObject.parseObject(session, UserSession.class);
            userSession.setConnectionState(ImConnectStatusEnum.OFFLINE_STATUS.getCode());
            map.put(client.getClientType(), JSONObject.toJSONString(userSession));
        }
    }

    public static UserClientDto createClient(NioSocketChannel socketChannel) {
        AttributeKey<String> userIdAttr = AttributeKey.valueOf(Constants.USER_ID);
        AttributeKey<Integer> appIdAttr = AttributeKey.valueOf(Constants.APP_ID);
        AttributeKey<Integer> clientTypeAttr = AttributeKey.valueOf(Constants.CLIENT_TYPE);
        return new UserClientDto(socketChannel.attr(userIdAttr).get(), socketChannel.attr(appIdAttr).get(), socketChannel.attr(clientTypeAttr).get());
    }
}
