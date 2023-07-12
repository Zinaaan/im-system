package com.zinan.im.tcp.utils;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.zinan.im.codec.config.BootstrapConfig;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

/**
 * @author lzn
 * @date 2023/07/11 21:54
 * @description RabbitMq factory
 */
public class MqFactory {

    private static ConnectionFactory factory = null;

    private static final Map<String, Channel> CHANNEL_MAP = new ConcurrentHashMap<>();

    public static Connection getConnection() throws IOException, TimeoutException {
        return factory.newConnection();
    }

    public static Channel getChannel(String channelName) throws IOException, TimeoutException {
        Channel channel = CHANNEL_MAP.get(channelName);
        if (channel == null) {
            channel = getConnection().createChannel();
            CHANNEL_MAP.put(channelName, channel);
        }

        return channel;
    }

    public static void init(BootstrapConfig.Rabbitmq rabbitmq) {
        if (factory == null) {
            factory = new ConnectionFactory();
            factory.setHost(rabbitmq.getHost());
            factory.setPort(rabbitmq.getPort());
            factory.setUsername(rabbitmq.getUserName());
            factory.setPassword(rabbitmq.getPassword());
            factory.setVirtualHost(rabbitmq.getVirtualHost());
        }
    }

}
