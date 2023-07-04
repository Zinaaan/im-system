package com.zinan.im.tcp.utils;

import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lzn
 * @date 2023/07/04 14:08
 * @description Session holder for the login users
 */
public class SessionSocketHolder {

    private static final Map<String, NioSocketChannel> CHANNELS = new ConcurrentHashMap<>();

    public static void put(String userId, NioSocketChannel socketChannel) {
        CHANNELS.put(userId, socketChannel);
    }

    public static NioSocketChannel get(String userId) {
        return CHANNELS.get(userId);
    }
}
