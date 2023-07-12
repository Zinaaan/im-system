package com.zinan.im.tcp.publisher;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import com.zinan.im.tcp.utils.MqFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lzn
 * @date 2023/07/11 22:01
 * @description Message producer for Rabbit Mq
 */
@Slf4j
public class MessageProducer {

    public static void sendMessage(Object message) {

        Channel channel = null;
        String channelName = "";

        try {
            channel = MqFactory.getChannel(channelName);
            channel.basicPublish(channelName, "", null, JSONObject.toJSONString(message).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Send message error: {}", e.getMessage());
        }
    }
}
