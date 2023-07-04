package com.zinan.im.tcp.utils;

import com.zinan.im.common.model.UserClientDto;
import io.netty.channel.socket.nio.NioSocketChannel;

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
}
